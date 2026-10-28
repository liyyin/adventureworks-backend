package com.refresh.customer_api.repository;

import com.refresh.customer_api.dto.SalesOrderWithCustomerResponse;
import com.refresh.customer_api.entity.SalesOrderHeader;
import com.refresh.customer_api.repository.projection.SalesOrderSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalesOrderHeaderRepository extends JpaRepository<SalesOrderHeader,Integer> {

    // jangan lupa selalu countQuerry kalo misalnya ini ada pagianation mau native atau JPQL
    // dan kalo pake JPQL sekalu biasakan aja langsung select new DTO langsung
    @Query(value = """
             SELECT r.Name
             FROM Sales.SalesOrderHeaderSalesReason hr
             JOIN Sales.SalesReason r ON r.SalesReasonID = hr.SalesReasonID
             WHERE hr.SalesOrderID = :salesOrderId
             ORDER BY r.Name
            """, nativeQuery = true
    )
    List<String> findSalesReasonNames(@Param("salesOrderId") Integer salesOrderId);

    @Query(
            value = """
                        select h
                        from SalesOrderHeader h
                        join fetch h.customer c
                        left join fetch c.person p
                        left join fetch c.store s
                        left join fetch h.details d
                        left join fetch d.product pr
                        where h.id = :id
                    """
    )
    Optional<SalesOrderHeader> findDetailOrderById(@Param("id")Integer id);

    @Query(value = """
            SELECT
                h.SalesOrderID AS id,
                h.SalesOrderNumber AS salesOrderNumber,
                h.OrderDate AS orderDate,
                h.Status AS status,
                h.CustomerID AS customerId,
                NULLIF(CONCAT_WS(' ',p.FirstName,p.MiddleName,p.LastName),'') AS personName,
                s.Name AS storeName,
                h.TotalDue AS totalDue
            FROM Sales.SalesOrderHeader h
            JOIN Sales.Customer c ON c.CustomerID = h.CustomerID
            LEFT JOIN Person.Person p ON p.BusinessEntityID = c.PersonID
            LEFT JOIN Sales.Store s ON s.BusinessEntityID = c.StoreID
            """,
            countQuery = "SELECT COUNT(*) FROM Sales.SalesOrderHeader",
            nativeQuery = true)
    Page<SalesOrderSummaryView> findSummaries(Pageable pageable);

    @Query(
        value = """
               select new com.refresh.customer_api.dto.SalesOrderWithCustomerResponse(h.id,
               h.salesOrderNumber,
               h.orderDate,
               h.status,
               h.customerId,
               case when p.id is null then null
                    else concat(p.firstName, ' ', coalesce(concat(p.middleName, ' '), ''), p.lastName)
               end,
               s.name,
               h.totalDue)
        from SalesOrderHeader h
        join h.customer c
        left join c.person p
        left join c.store s
        """,
    countQuery = "select count(h) from SalesOrderHeader h")
    Page<SalesOrderWithCustomerResponse> findSummariesJpql(Pageable pageable);

    @Modifying
    @Query(value = """
            INSERT INTO Sales.SalesOrderHeaderSalesReason (SalesOrderID, SalesReasonID)
            VALUES (:salesOrderId,:salesReasonId)
            """,nativeQuery = true)
    void insertSalesReason(@Param("salesOrderId") Integer salesOrderId,
                           @Param("salesReasonId") Integer salesReasonId);
}
