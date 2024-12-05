package com.example.project_noah_ritcey.objects;

import com.example.project_noah_ritcey.entities.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartPizza {

    private Pizzasize pizzaSize;
    private Pizzacrust pizzacrust;
    private Integer quantity;
    private BigDecimal priceEach;
    private BigDecimal totalPrice;
    private List<Pizzatopping> toppings = new ArrayList<>();
}
