package com.refresh.customer_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Product",schema = "Production")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "ProductNumber")
    private String productNumber;

    @Column(name = "Color")
    private String color;

    @Column(name = "StandardCost")
    private BigDecimal standardCost;

    @Column(name = "ListPrice")
    private BigDecimal listPrice;

    @Column(name = "SafetyStockLevel")
    private Integer safetyStockLevel;

    @Column(name = "ReorderPoint")
    private Integer reorderPoint;

    @Column(name = "DaysToManufacture")
    private Integer daysToManufacture;

    @Column(name = "SellStartDate")
    private LocalDateTime sellStartDate;

    @Column(name = "SellEndDate")
    private LocalDateTime sellEndDate;

    @Column(name = "ModifiedDate")
    private LocalDateTime modifiedDate;

    // ini dijalankan otomatis pada saat INSERT dan UPDATE
    @PrePersist
    @PreUpdate
    void touchModifiedDate(){
        this.modifiedDate = LocalDateTime.now();
    }
}
