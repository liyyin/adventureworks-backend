package com.refresh.customer_api.service;

import com.refresh.customer_api.dto.ProductResponse;
import com.refresh.customer_api.dto.ProductSelectorResponse;
import com.refresh.customer_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> search(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(ProductResponse::from);
    }

    public Optional<ProductResponse> findById(Integer id) {
        return productRepository.findById(id)
                .map(ProductResponse::from);
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
}