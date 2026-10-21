package com.refresh.customer_api.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SalesOrderHeader",schema = "Sales")
@Getter
public class SalesOrderHeader {

    @Id
    @Column(name = "SalesOrderID")
    private Integer id;

    @Column(name = "SalesOrderNumber",insertable = false,updatable = false)
    private String salesOrderNumber;

    @Column(name = "OrderDate")
    private LocalDateTime orderDate;

    @Column(name = "DueDate")
    private LocalDateTime dueDate;

    @Column(name = "ShipDate")
    private LocalDateTime shipDate;

    @Column(name = "Status")
    private Integer status;

    @Column(name = "CustomerID")
    private Integer customerId;

    @Column(name = "SubTotal")
    private BigDecimal subTotal;

    @Column(name = "TaxAmt")
    private BigDecimal taxAmt;

    @Column(name = "Freight")
    private BigDecimal freight;

    @Column(name = "TotalDue",insertable = false,updatable = false)
    private BigDecimal totalDue;

    @OneToMany(mappedBy = "salesOrder")
    private List<SalesOrderDetail> details =new ArrayList<>();

    //Many to Many ini biasa di pake kalo misalnya dua data tersebut udah masih saling berhubungan
}
