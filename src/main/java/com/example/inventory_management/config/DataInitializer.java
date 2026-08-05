package com.example.inventory_management.config;

import com.example.inventory_management.model.Company;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.CompanyRepository;
import com.example.inventory_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Company demoCompany = companyRepository
                    .findByNameIgnoreCase("Demo Company")
                    .orElseGet(() -> companyRepository.save(new Company("Demo Company")));

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "ROLE_ADMIN",
                        demoCompany
                );
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println(">>> Created default user: admin / admin123 (Demo Company)");
            }

            if (!userRepository.existsByUsername("staff")) {
                User staff = new User(
                        "staff",
                        passwordEncoder.encode("staff123"),
                        "ROLE_STAFF",
                        demoCompany
                );
                staff.setEnabled(true);
                userRepository.save(staff);
                System.out.println(">>> Created default user: staff / staff123 (Demo Company)");
            }
        };
    }
}
