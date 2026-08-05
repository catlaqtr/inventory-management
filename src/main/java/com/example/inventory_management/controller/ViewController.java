package com.example.inventory_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Resolves to src/main/resources/templates/login.html
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard"; // Resolves to src/main/resources/templates/dashboard.html
    }
}