package com.example.inventory_management.controller;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.model.User;
import com.example.inventory_management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            User registeredUser = userService.registerUser(userDTO);
            return ResponseEntity.ok("User registered successfully: " + registeredUser.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}