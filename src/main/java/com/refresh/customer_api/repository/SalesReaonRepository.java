package com.refresh.customer_api.repository;

import com.refresh.customer_api.entity.SalesReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesReaonRepository extends JpaRepository<SalesReason,Integer> {
}
