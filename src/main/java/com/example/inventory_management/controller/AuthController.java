package com.example.inventory_management.controller;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerCompany(
            @ModelAttribute("user") UserDTO userDTO,
            Model model
    ) {
        try {
            userService.registerCompanyAdmin(userDTO);
            return "redirect:/login?registered=true";
        } catch (RuntimeException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String resetAdminPassword(
            @ModelAttribute("user") UserDTO userDTO,
            Model model
    ) {
        try {
            userService.resetCompanyAdminPassword(userDTO);
            return "redirect:/login?passwordReset=true";
        } catch (RuntimeException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "forgot-password";
        }
    }
}
