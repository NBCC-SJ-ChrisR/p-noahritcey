package com.example.project_noah_ritcey.controllers;

import com.example.project_noah_ritcey.entities.*;
import com.example.project_noah_ritcey.services.PizzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/buildPizza")
public class PizzaController {
    private final PizzaService pizzaService;

    @GetMapping
    public String showBuildPizza(Model model) {
        model.addAttribute("sizes", pizzaService.getAllPizzasize());
        model.addAttribute("crusts", pizzaService.getAllPizzacrust());
        model.addAttribute("toppings", pizzaService.getActiveToppings());
        return "buildPizza";
    }
}
