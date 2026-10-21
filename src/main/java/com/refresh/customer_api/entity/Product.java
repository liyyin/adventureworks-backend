package com.refresh.customer_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Product",schema = "Production")
@Getter
public class Product {
    @Id
    @Column(name = "ProductID")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "ProductNumber")
    private String productNumber;

    @Column(name = "Color")
    private String color;

    @Column(name = "ListPrice")
    private BigDecimal listPrice;

    @Column(name = "SellStartDate")
    private LocalDateTime sellStartDate;
}
