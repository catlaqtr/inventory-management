package com.example.inventory_management.service;

import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.security.CompanyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username
                ));

        if (user.getCompany() == null) {
            throw new UsernameNotFoundException(
                    "User is not linked to a company: " + username
            );
        }

        return new CompanyUserDetails(user);
    }
}
