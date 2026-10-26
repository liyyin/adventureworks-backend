package com.refresh.customer_api.service;


import com.refresh.customer_api.dto.SalesOrderDetailResponse;
import com.refresh.customer_api.dto.SalesOrderSummaryResponse;
import com.refresh.customer_api.dto.SalesOrderWithCustomerResponse;
import com.refresh.customer_api.entity.SalesOrderHeader;
import com.refresh.customer_api.exception.ResourceNotFoundException;
import com.refresh.customer_api.repository.SalesOrderHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class SalesOrderService {

    private final SalesOrderHeaderRepository salesOrderHeaderRepository;

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

    public Page<SalesOrderWithCustomerResponse> listCustomerStore(Pageable pageable){
        return salesOrderHeaderRepository.findSummaries(pageable)
                .map(SalesOrderWithCustomerResponse::from);
    }

    public Page<SalesOrderWithCustomerResponse> listCustomerJpql(Pageable pageable){
        return salesOrderHeaderRepository.findSummariesJpql(pageable);
    }
}
