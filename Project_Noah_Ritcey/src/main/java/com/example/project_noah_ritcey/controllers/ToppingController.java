package com.example.project_noah_ritcey.controllers;


import com.example.project_noah_ritcey.services.ToppingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/employeeDashboard")
public class ToppingController {

    private final ToppingService toppingService;


    @PostMapping("/addTopping")
    public String addTopping(@RequestParam String name, @RequestParam Float price, Model model) {
        try {
            toppingService.addTopping(name, price);
            model.addAttribute("success", "Topping added");
        } catch (Exception e) {
            model.addAttribute("error", "Error adding topping: " + e.getMessage());
        }
        model.addAttribute("toppings", toppingService.getAllToppings());
        model.addAttribute("activeTab", "toppings");
        return "employeeDashboard";
    }

    @PostMapping("/deactivateTopping/{id}")
    public String deactivateTopping(@PathVariable Integer id, Model model) {
        try {
            toppingService.deactivateTopping(id);
            model.addAttribute("success", "Topping deactivated");
        } catch (Exception e) {
            model.addAttribute("error", "Error deactivating topping: " + e.getMessage());
        }
        model.addAttribute("toppings", toppingService.getAllToppings());
        model.addAttribute("activeTab", "toppings");
        return "employeeDashboard";
    }

    @PostMapping("/reactivateTopping/{id}")
    public String reactivateTopping(@PathVariable Integer id, Model model) {
        try {
            toppingService.reactivateTopping(id);
            model.addAttribute("success", "Topping reactivated");
        } catch (Exception e) {
            model.addAttribute("error", "Error reactivating topping: " + e.getMessage());
        }
        model.addAttribute("toppings", toppingService.getAllToppings());
        model.addAttribute("activeTab", "toppings");
        return "employeeDashboard";
    }

    @PostMapping("/editTopping/{id}")
    public String editTopping(@PathVariable Integer id, @RequestParam String name, @RequestParam Float price, Model model) {
        try{
            toppingService.editTopping(id, name, price);
            model.addAttribute("success", "Topping edited");
        }catch (Exception e){
            model.addAttribute("error", "Error editing topping: " + e.getMessage());
        }
        model.addAttribute("toppings", toppingService.getAllToppings());
        model.addAttribute("activeTab", "toppings");
        return "employeeDashboard";
    }
}
