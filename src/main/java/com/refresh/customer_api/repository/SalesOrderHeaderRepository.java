package com.refresh.customer_api.repository;

import com.refresh.customer_api.entity.SalesOrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesOrderHeaderRepository extends JpaRepository<SalesOrderHeader,Integer> {

    @Query(value = """
            SELECT r.Name
            FROM Sales.SalesOrderHeaderSalesReason hr
            JOIN Sales.SalesReason r ON r.SalesReasonID = hr.SalesReasonID
            WHERE hr.SalesOrderID = :salesOrderId
            ORDER BY r.Name
           """, nativeQuery = true
    )
    List<String> findSalesReasonNames(@Param("salesOrderId") Integer salesOrderId);
}
