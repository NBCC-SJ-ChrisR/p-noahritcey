package com.example.project_noah_ritcey.services;

import com.example.project_noah_ritcey.entities.*;
import com.example.project_noah_ritcey.objects.CartPizza;
import com.example.project_noah_ritcey.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public CartPizza createCartPizza(Pizzasize size, Pizzacrust crust, List<Pizzatopping> toppings, int quantity) {

        CartPizza cartPizza = new CartPizza();
        cartPizza.setPizzacrust(crust);
        cartPizza.setPizzaSize(size);
        cartPizza.setQuantity(quantity);
        cartPizza.setToppings(new ArrayList<>(toppings));

        Float basePrice = size.getPrice() + crust.getPrice();

        Float toppingPrice = toppings.stream().map(Pizzatopping::getPrice).reduce(0.0f,Float::sum);

        Float pricePerPizza = basePrice + toppingPrice;

        cartPizza.setPriceEach(pricePerPizza);

        cartPizza.setTotalPrice(pricePerPizza * quantity);

        return cartPizza;

    }

    // Convert CartPizza to Pizza entity when creating order
    public Pizza convertCartPizzaToEntity(CartPizza cartPizza, Order order) {
        Pizza pizza = new Pizza();
        pizza.setOrder(order);
        pizza.setPizzaSize(cartPizza.getPizzaSize());
        pizza.setPizzaCrust(cartPizza.getPizzacrust());
        pizza.setQuantity(cartPizza.getQuantity());
        pizza.setPriceEach(cartPizza.getPriceEach());
        pizza.setTotalPrice(cartPizza.getTotalPrice());

        return pizza;
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

    public Float calculateCartSubTotal(List<CartPizza> cartPizzas) {
        if (cartPizzas == null) return 0.0f;
        float subtotal = 0.0f;
        for (CartPizza pizza : cartPizzas) {
            subtotal += pizza.getTotalPrice();
        }
        return subtotal;
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
