package com.example.project_noah_ritcey.controllers;

import com.example.project_noah_ritcey.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.project_noah_ritcey.services.ToppingService;

@Controller
@RequiredArgsConstructor
public class EmployeeLoginController {

    private final ToppingService toppingService;
    private final OrderService orderService;


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/employeeDashboard")
    public String dashboard(Model model) {
        model.addAttribute("toppings", toppingService.getAllToppings());
        model.addAttribute("orders",orderService.getAllOrders());
        return "employeeDashboard";
    }
}
