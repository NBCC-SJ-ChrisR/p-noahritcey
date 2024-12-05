package com.example.project_noah_ritcey.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ColumnDefault("0.00")
    @Column(name = "subTotal", nullable = false, precision = 8, scale = 2)
    private BigDecimal subTotal;

    @ColumnDefault("0.00")
    @Column(name = "hst", nullable = false, precision = 8, scale = 2)
    private BigDecimal hst;

    @ColumnDefault("0.00")
    @Column(name = "orderTotal", nullable = false, precision = 8, scale = 2)
    private BigDecimal orderTotal;

    @ColumnDefault("0.00")
    @Column(name = "amountPaid", nullable = false, precision = 8, scale = 2)
    private BigDecimal amountPaid;

    @ColumnDefault("'PENDING'")
    @Column(name = "orderStatus", nullable = false, length = 12)
    private String orderStatus;

    @ColumnDefault("0")
    @Column(name = "delivery", nullable = false)
    private Byte delivery;

    @ColumnDefault("current_timestamp()")
    @Column(name = "deliveryDate", nullable = false)
    private Instant deliveryDate;

    @ColumnDefault("current_timestamp()")
    @Column(name = "orderPlacedDate", nullable = false)
    private Instant orderPlacedDate;

}