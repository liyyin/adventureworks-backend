package com.refresh.customer_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "Store",schema = "Sales")
@Getter
public class Store {

    @Column(name = "BusinessEntityID")
    @Id
    private Integer id;

    @Column(name = "Name")
    private String name;
}
