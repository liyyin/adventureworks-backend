package com.refresh.customer_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "SalesOrderDetail", schema = "Sales")
@Getter
@Setter
public class SalesOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SalesOrderDetailID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SalesOrderID")
    private SalesOrderHeader salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductID")
    private Product product;

    @Column(name = "SpecialOfferID")
    private Integer specialOfferId;

    @Column(name = "OrderQty")
    private Integer orderQty;

    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;

    @Column(name = "UnitPriceDiscount")
    private BigDecimal unitPriceDiscount;

    @Column(name = "LineTotal",insertable = false,updatable = false)
    private BigDecimal lineTotal;
}
