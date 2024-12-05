package com.example.project_noah_ritcey.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @ColumnDefault("current_timestamp()")
    @Column(name = "createdate", nullable = false)
    private Instant createdate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empAddedBy", nullable = false)
    private Employee empAddedBy;

    @ColumnDefault("1")
    @Column(name = "isActive", nullable = false)
    private Byte isActive;

    @OneToMany(mappedBy = "pizzaTopping")
    private Set<PizzatoppingMap> pizzatoppingMaps = new LinkedHashSet<>();

}