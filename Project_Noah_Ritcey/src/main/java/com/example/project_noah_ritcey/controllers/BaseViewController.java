package com.example.project_noah_ritcey.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
public class BaseViewController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
