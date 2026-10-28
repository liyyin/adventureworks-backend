package com.refresh.customer_api.service;

import com.refresh.customer_api.dto.ProductRequest;
import com.refresh.customer_api.dto.ProductResponse;
import com.refresh.customer_api.dto.ProductSelectorResponse;
import com.refresh.customer_api.entity.Product;
import com.refresh.customer_api.exception.BadRequestException;
import com.refresh.customer_api.exception.ConflictException;
import com.refresh.customer_api.exception.ResourceNotFoundException;
import com.refresh.customer_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> search(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(ProductResponse::from);
    }

    public Page<ProductResponse> searchManualQuery(String keyword,Pageable pageable){
        return productRepository.searchByKeyWordNative(keyword,pageable)
                .map(ProductResponse::from);
    }

    public ProductResponse findById(Integer id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product",id));
    }

    public List<ProductSelectorResponse> getAllWithQuery(){
        return productRepository.findForSelect()
                .stream()
                .map(ProductSelectorResponse::from)
                .toList();
    }

    public List<ProductSelectorResponse> getAllWithProjectionByJpa(){
        return productRepository.findAllByOrderByNameDesc();
    }

    @Transactional
    public ProductResponse create(ProductRequest request){
        validateDates(request);

        if(productRepository.existsByName(request.name().trim())){
            throw new ConflictException("Nama Produk '" + request.name()+ "' sudah dipakai");
        }
        if (productRepository.existsByProductNumber(request.productNumber().trim())){
            throw new ConflictException("Nomor Produk '"+request.productNumber()+"' sudah dipakai");
        }

        Product product = new Product();
        applyRequest(product,request);
        try {
            Product saved = productRepository.save(product);
            return ProductResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            log.warn("Gagal menyimpan produk '{}': {}", request.name(), e.getMostSpecificCause().getMessage());
            throw new ConflictException(
                    "Nama atau nomor produk sudah dipakai. Silakan muat ulang data dan coba lagi.");
        }
    }

    @Transactional
    public ProductResponse update(Integer id,ProductRequest request){
        Product product = productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product",id));

        validateDates(request);

        if (productRepository.existsByNameAndIdNot(request.name().trim(), id)){
            throw new ConflictException("Nama Produk '"+request.name() +"' udah dipakai");
        }
        if(productRepository.existsByProductNumberAndIdNot(request.productNumber().trim(), id)){
            throw new ConflictException("Nomor Produk '" +request.productNumber()+"' udah dipakai");
        }

        applyRequest(product,request);

        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Integer id){
        Product product = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product",id));
        try {
            productRepository.delete(product);
            productRepository.flush();
        }catch (DataIntegrityViolationException e){
            log.warn("Gagal menghapus produk {}: {}", id, e.getMostSpecificCause().getMessage());
            throw new ConflictException(
                    "Produk '" + product.getName() + "' tidak bisa dihapus karena masih dipakai di Sales Order");
        }
    }

    private void applyRequest(Product product,ProductRequest request){
        product.setName(request.name().trim());
        product.setProductNumber(request.productNumber().trim());
        product.setColor(request.color());
        product.setStandardCost(request.standardCost());
        product.setListPrice(request.listPrice());
        product.setSafetyStockLevel(request.safetyStockLevel());
        product.setReorderPoint(request.reorderPoint());
        product.setDaysToManufacture(request.daysToManufacture());
        product.setSellStartDate(request.sellStartDate());
        product.setSellEndDate(request.sellEndDate());
    }
    private void validateDates(ProductRequest request) {
        if (request.sellEndDate() != null && request.sellEndDate().isBefore(request.sellStartDate())) {
            throw new BadRequestException("Tanggal akhir jual tidak boleh sebelum tanggal mulai jual");
        }
    }
}