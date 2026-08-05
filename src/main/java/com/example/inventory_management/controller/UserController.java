package com.example.inventory_management.controller;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.model.User;
import com.example.inventory_management.security.CompanyContext;
import com.example.inventory_management.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getUsersForCurrentCompany());
        model.addAttribute("currentUserId", CompanyContext.requireCurrentUser().getUserId());
        return "users";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user-form";
    }

    @PostMapping("/save")
    public String saveUser(
            @ModelAttribute("user") UserDTO userDTO,
            Model model
    ) {
        try {
            userService.createCompanyUser(userDTO);
            return "redirect:/users?created=true";
        } catch (RuntimeException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "user-form";
        }
    }

    @GetMapping("/{id}/change-password")
    public String showChangePasswordForm(@PathVariable Long id, Model model) {
        try {
            User target = userService.getCompanyUser(id);
            if (CompanyContext.requireCurrentUser().getUserId().equals(id)) {
                return "redirect:/users?ownPassword=true";
            }
            model.addAttribute("targetUser", target);
            model.addAttribute("user", new UserDTO());
            return "change-password";
        } catch (RuntimeException exception) {
            return "redirect:/users";
        }
    }

    @PostMapping("/{id}/change-password")
    public String changePassword(
            @PathVariable Long id,
            @ModelAttribute("user") UserDTO userDTO,
            Model model
    ) {
        try {
            userService.changePasswordForCompanyUser(id, userDTO.getPassword(), userDTO.getConfirmPassword());
            return "redirect:/users?passwordChanged=true";
        } catch (RuntimeException exception) {
            try {
                model.addAttribute("targetUser", userService.getCompanyUser(id));
            } catch (RuntimeException ignored) {
                return "redirect:/users";
            }
            model.addAttribute("errorMessage", exception.getMessage());
            return "change-password";
        }
    }
}
