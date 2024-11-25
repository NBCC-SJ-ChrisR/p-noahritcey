package com.example.project_noah_ritcey.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ColumnDefault("0")
    @Column(name = "subTotal", nullable = false)
    private Float subTotal;

    @ColumnDefault("0")
    @Column(name = "hst", nullable = false)
    private Float hst;

    @ColumnDefault("0")
    @Column(name = "orderTotal", nullable = false)
    private Float orderTotal;

    @ColumnDefault("'PENDING'")
    @Column(name = "orderStatus", nullable = false, length = 12)
    private String orderStatus;

    @ColumnDefault("current_timestamp()")
    @Column(name = "deliveryDate", nullable = false)
    private Instant deliveryDate;

    @ColumnDefault("current_timestamp()")
    @Column(name = "orderPlacedDate", nullable = false)
    private Instant orderPlacedDate;

    @OneToMany(mappedBy = "order")
    private Set<Pizza> pizzas = new LinkedHashSet<>();

}