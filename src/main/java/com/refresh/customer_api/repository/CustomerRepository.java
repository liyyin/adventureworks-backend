package com.refresh.customer_api.repository;

import com.refresh.customer_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}
