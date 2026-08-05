package com.example.inventory_management.config;

import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    // Spring will automatically inject the PasswordEncoder defined in SecurityConfig here
    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println(">>> Created default user: admin / admin123");
            }

            if (!userRepository.existsByUsername("staff")) {
                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("staff123"));
                staff.setRole("ROLE_STAFF");
                staff.setEnabled(true);
                userRepository.save(staff);
                System.out.println(">>> Created default user: staff / staff123");
            }
        };
    }
}