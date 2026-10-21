package com.refresh.customer_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "SalesReason",schema = "Sales")
@Getter
@RequiredArgsConstructor
public class SalesReason {

    @Id
    @Column(name = "SalesReasonID")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "ReasonType")
    private String reasonType;
}
