package com.example.project_noah_ritcey.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "pizzasize")
public class Pizzasize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pizzaSize_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 16)
    private String name;

    @ColumnDefault("0")
    @Column(name = "price", nullable = false)
    private Float price;

    @OneToMany(mappedBy = "pizzaSize")
    private Set<Pizza> pizzas = new LinkedHashSet<>();

}