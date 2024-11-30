package com.example.project_noah_ritcey.controllers;


import com.example.project_noah_ritcey.services.OrderService;
import com.example.project_noah_ritcey.services.ToppingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
@RequestMapping("/employeeDashboard")
public class OrderController {
    private final OrderService orderService;
    private final ToppingService toppingService;

    @PostMapping("/changeOrderStatus/{id}")
    public String changeOrderToFulFilled(@PathVariable Integer id, RedirectAttributes redirectAtts) {

        try{
            orderService.changeStatusToFilled(id);
            redirectAtts.addFlashAttribute("Success", "Order fulfilled");
        }catch (Exception e){
            redirectAtts.addFlashAttribute("Error", "Error changing order status " + e.getMessage());
        }
        return "redirect:/employeeDashboard";
    }

    @GetMapping("/getOrdersByStatus/{status}")
    public String getOrdersByStatus(@PathVariable String status, Model model) {
        model.addAttribute("orders", orderService.getOrdersByStatus(status));
        model.addAttribute("toppings", toppingService.getAllToppings());
        model.addAttribute("activeTab", "orders");
        model.addAttribute("orderStatus", status);
        return "employeeDashboard";
    }
}
