package com.example.project_noah_ritcey.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pizzasize")
public class Pizzasize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pizzaSize_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 16)
    private String name;

    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

}