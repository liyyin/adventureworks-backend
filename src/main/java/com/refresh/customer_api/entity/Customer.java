package com.refresh.customer_api.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "Customer",schema = "Sales")
@Getter
public class Customer {

    @Id
    @Column(name = "CustomerID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PersonID")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StoreID")
    private Store store;
}
