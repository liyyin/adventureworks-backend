package com.refresh.customer_api.service;


import com.refresh.customer_api.dto.*;
import com.refresh.customer_api.entity.*;
import com.refresh.customer_api.enums.SalesOrderStatus;
import com.refresh.customer_api.exception.BadRequestException;
import com.refresh.customer_api.exception.ConflictException;
import com.refresh.customer_api.exception.ResourceNotFoundException;
import com.refresh.customer_api.repository.CustomerRepository;
import com.refresh.customer_api.repository.ProductRepository;
import com.refresh.customer_api.repository.SalesOrderHeaderRepository;
import com.refresh.customer_api.repository.SalesReaonRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class SalesOrderService {

    private final SalesOrderHeaderRepository salesOrderHeaderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;
    private final SalesReaonRepository salesReaonRepository;

    public Page<SalesOrderSummaryResponse> list(Pageable pageable) {
        return salesOrderHeaderRepository.findAll(pageable)
                .map(SalesOrderSummaryResponse::from);
    }

    public SalesOrderDetailResponse getById(Integer id) {
        SalesOrderHeader header = salesOrderHeaderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Sales Order",id));

        List<String> reasons = salesOrderHeaderRepository.findSalesReasonNames(id);

        return SalesOrderDetailResponse.from(header,reasons);
    }

    public SalesOrderDetailResponse getDetailById(Integer id){
        SalesOrderHeader header = salesOrderHeaderRepository.findDetailOrderById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Sales Order",id));

        List<String> reasons = salesOrderHeaderRepository.findSalesReasonNames(id);

        return SalesOrderDetailResponse.from(header,reasons);
    }

    public Page<SalesOrderWithCustomerResponse> listCustomerStore(Pageable pageable){
        return salesOrderHeaderRepository.findSummaries(pageable)
                .map(SalesOrderWithCustomerResponse::from);
    }

    public Page<SalesOrderWithCustomerResponse> listCustomerJpql(Pageable pageable){
        return salesOrderHeaderRepository.findSummariesJpql(pageable);
    }

    @Transactional
    public SalesOrderDetailResponse create(SalesOrderCreateRequest request){
        LocalDateTime now = LocalDateTime.now();
        //cek tanggal due dan dan order date validasi
        if(request.dueDate().isBefore(now)){
            throw new BadRequestException("Tanggal Jatuh Tempo tidak dapat sebelum tanggal order");
        }
        // cek customer exists
        Customer customer = customerRepository.findById(request.customerId()).orElseThrow(()-> new BadRequestException("Customer TIdak di temukan"));

        // ambil semua product
        Map<Integer,Product> products = loadProduct(request.items());

        // cek sales reason(optional)
        Set<Integer> reasonIds = request.salesReasonIds() == null
                ?Set.of() : Set.copyOf(request.salesReasonIds());

        Set<Integer> foundReasonIds = salesReaonRepository.findAllById(reasonIds).stream()
                .map(SalesReason::getId)
                .collect(Collectors.toSet());

        List<Integer> missingReasons = reasonIds.stream()
                .filter(id->!foundReasonIds.contains(id))
                .sorted()
                .toList();
        if (!missingReasons.isEmpty()) {
            throw new BadRequestException("Sales reason tidak ditemukan: " + missingReasons);
        }

        // buat sales order header
        SalesOrderHeader header = new SalesOrderHeader();
        header.setCustomer(customer);
        header.setOrderDate(now);
        header.setDueDate(request.dueDate());
        header.changeStatus(SalesOrderStatus.IN_PROCESS);   // 1 = In process
        header.setBillToAddressId(request.billToAddressId());
        header.setShipToAddressId(request.shipToAddressId());
        header.setShipMethodId(request.shipMethodId());
        header.setTaxAmt(BigDecimal.ZERO);
        header.setFreight(BigDecimal.ZERO);

        //Buat Item, Harga diambil dari database
        BigDecimal subTotal = addItems(header,request.items(),products);
        header.setSubTotal(subTotal);

        // 7. simpan header + item (cascade)
        try {
            salesOrderHeaderRepository.saveAndFlush(header);
            // adding reason
            for (Integer reasonId:reasonIds){
                salesOrderHeaderRepository.insertSalesReason(header.getId(),reasonId);
            }
        }catch (DataIntegrityViolationException e){
            log.warn("Gagal menyimpan sales order: {}", e.getMostSpecificCause().getMessage());
            throw new ConflictException(
                    "Sales order tidak bisa disimpan. Periksa alamat, metode pengiriman, "
                            + "atau produk yang tidak tersedia untuk dijual.");
        }

        // 8. Ambil Ulang dari database supaya kolom computed(SalesOrderNumber, TotalDue, LineTotal) terisi
        Integer newId = header.getId();
        entityManager.clear();
        return getDetailById(newId);
    }

    // Ambil semua produk dalam satu query, dan pastikan semuanya ada
    private Map<Integer, Product> loadProduct(List<SalesOrderItemRequest> items){
        Set<Integer> productIds = items.stream()
                .map(SalesOrderItemRequest::productId)
                .collect(Collectors.toSet());

        Map<Integer,Product> products = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId,p->p));

        List<Integer> missing = productIds.stream()
                .filter(id -> !products.containsKey(id))
                .sorted()
                .toList();

        if (!missing.isEmpty()){
            throw new BadRequestException("Produk Tidak ditemukan "+ missing);
        }

        return products;
    }

    // Buat item dan tambahkan ke header, lalu kembalikan subtotal
    private BigDecimal addItems(SalesOrderHeader header
            ,List<SalesOrderItemRequest> items
            ,Map<Integer,Product> products){
        BigDecimal subTotal = BigDecimal.ZERO;
        for (SalesOrderItemRequest item: items){
            Product product = products.get(item.productId());

            SalesOrderDetail detail = new SalesOrderDetail();
            detail.setProduct(product);
            detail.setOrderQty(item.orderQty());
            detail.setUnitPrice(product.getListPrice());
            detail.setUnitPriceDiscount(BigDecimal.ZERO);
            detail.setSpecialOfferId(1);
            header.addDetail(detail);

            subTotal = subTotal.add(product.getListPrice().multiply(BigDecimal.valueOf(item.orderQty())));
        }
        return subTotal;
    }

    //Simpan perubahan ke database, lalu ambil ulang order yang lengkap
    private SalesOrderDetailResponse flushAndReload(Integer id){
        salesOrderHeaderRepository.flush();
        entityManager.clear();
        return getDetailById(id);
    }

    //ambil order cek aturan transisi, lalu ubah statusnya
    private SalesOrderHeader transition(Integer id, SalesOrderStatus target){
        SalesOrderHeader header = salesOrderHeaderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales Order",id));

        SalesOrderStatus current = header.getStatusEnum();
        if(!current.canTransitionTo(target)){
            throw new ConflictException("Order " + header.getSalesOrderNumber()
            + " berstatus " + current.getLabel()
            + " dan tidak bisa diubah menjadi " + target.getLabel());
        }
        header.changeStatus(target);
        return header;
    }

    @Transactional
    public SalesOrderDetailResponse update(Integer id,SalesOrderUpdateRequest request){
        SalesOrderHeader header =salesOrderHeaderRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Sales Order",id));

        SalesOrderStatus status = header.getStatusEnum();

        if (!status.isEditable()){
            throw new ConflictException("Order " + header.getSalesOrderNumber()
                    + " berstatus " + status.getLabel()
                    + ". Hanya order berstatus In process yang bisa diubah.");
        }

        if (request.dueDate().isBefore(header.getOrderDate())){
            throw new BadRequestException("Tanggal jatuh tempo tidak boleh sebelum tanggal order");
        }

        Map<Integer,Product> products = loadProduct(request.items());


        try {
            header.setDueDate(request.dueDate());

            header.getDetails().clear();
            salesOrderHeaderRepository.flush();
            
            header.setSubTotal(addItems(header,request.items(),products));
            return flushAndReload(id);
        }catch (DataIntegrityViolationException e){
            log.warn("Gagal mengubah sales order {}: {}", id, e.getMostSpecificCause().getMessage());
            throw new ConflictException("Sales order tidak bisa diubah. Periksa produk yang tidak tersedia untuk dijual.");
        }
    }

    @Transactional
    public SalesOrderDetailResponse approve(Integer id) {
        transition(id, SalesOrderStatus.APPROVED);
        return flushAndReload(id);
    }

    @Transactional
    public SalesOrderDetailResponse ship(Integer id) {
        SalesOrderHeader header = transition(id, SalesOrderStatus.SHIPPED);
        header.setShipDate(LocalDateTime.now());
        return flushAndReload(id);
    }

    @Transactional
    public SalesOrderDetailResponse cancel(Integer id) {
        transition(id, SalesOrderStatus.CANCELLED);
        return flushAndReload(id);
    }
}
