package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.*;
import com.example.project_noah_ritcey.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaService {
    private final PizzaRepository pizzaRepository;
    private final PizzacrustRepository pizzacrustRepository;
    private final PizzatoppingRepository pizzatoppingRepository;
    private final PizzaToppingMapRepository toppingMapRepository;
    private final PizzasizeRepository pizzasizeRepository;

    public List<Pizzasize> getAllPizzasize() {
        return pizzasizeRepository.findAll();
    }
    public List<Pizzatopping> getAllPizzatopping() {
        return pizzatoppingRepository.findAll();
    }
    public List<Pizzacrust> getAllPizzacrust() {
        return pizzacrustRepository.findAll();
    }

    public List<Pizzatopping> getActiveToppings() {
        return pizzatoppingRepository.findByIsActive((byte)1);
    }

    public Pizza createPizza(Pizzasize size, Pizzacrust crust, List<Pizzatopping> toppings, int quantity) {

        Pizza pizza = new Pizza();
        pizza.setPizzaSize(size);
        pizza.setPizzaCrust(crust);
        pizza.setQuantity(quantity);

        Float basePrice = size.getPrice() + crust.getPrice();
        Float toppingPrice = 0.0f;
        for (Pizzatopping topping : toppings) {
            toppingPrice += topping.getPrice();
        }

        Float pricePerPizza = basePrice +toppingPrice;
        pizza.setPriceEach(pricePerPizza);
        pizza.setTotalPrice(pricePerPizza * quantity);

        Pizza savedPizza = pizzaRepository.save(pizza);

        for(Pizzatopping topping : toppings) {
            PizzaToppingMap map = new PizzaToppingMap();
            map.setPizza(savedPizza);
        }

        return savedPizza;
    }

    //Calc pizza price without writing a pizza to the db
    public Float calculatePrice(int sizeId, int crustId, List<Integer> toppingIds) {
        Pizzasize size = pizzasizeRepository.findById(sizeId).orElseThrow(()-> new RuntimeException("Size not found."));
        Pizzacrust crust = pizzacrustRepository.findById(crustId).orElseThrow(() -> new RuntimeException("Crust not found"));
        List<Pizzatopping> toppings = pizzatoppingRepository.findAllById(toppingIds);

        Float price = size.getPrice() + crust.getPrice();
        for (Pizzatopping topping : toppings) {
            price += topping.getPrice();
        }
        return price;
    }

    public List<PizzaToppingMap> getPizzaToppings(Integer pizzaId) {
        return toppingMapRepository.findByPizzaId(pizzaId);
    }

    //Calc the subtotal for pizzas in order
    public Float calculateSubTotal(List<Pizza> pizzas) {
        float subTotal = 0.0f;
        for (Pizza pizza : pizzas) {
           subTotal += pizza.getTotalPrice();
        }
        return subTotal;
    }

    //Calc tax on subtotal value
    public Float calculateTax(Float subTotal) {
        return subTotal * 0.15f;
    }
    //Calc total based on subtotal value
    public Float calculateTotalPrice(Float subtotal) {
        return subtotal * 1.15f;
    }
}
