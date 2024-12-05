package com.example.project_noah_ritcey.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pizzacrust")
public class Pizzacrust {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pizzaCrust_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 16)
    private String name;

    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "pizzaCrust")
    private Set<Pizza> pizzas = new LinkedHashSet<>();

}