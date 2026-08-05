package com.example.inventory_management.service;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        String defaultRole = (userDTO.getRole() != null && !userDTO.getRole().isEmpty())
                ? userDTO.getRole()
                : "ROLE_STAFF";

        User newUser = new User(userDTO.getUsername(), encodedPassword, defaultRole);
        return userRepository.save(newUser);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}