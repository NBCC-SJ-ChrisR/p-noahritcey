package com.example.project_noah_ritcey.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pizzatopping")
public class Pizzatopping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pizzaTopping_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "price", nullable = false)
    private Float price = 0.0f;

    @Column(name = "createdate", nullable = false, updatable = false)
    private Instant createdate = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empAddedBy", nullable = false)
    private Employee empAddedBy;

    @Column(name = "isActive", nullable = false)
    private Byte isActive = 1;
}