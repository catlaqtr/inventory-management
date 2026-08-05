package com.example.inventory_management.service;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.model.Company;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.CompanyRepository;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.security.CompanyContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerCompanyAdmin(UserDTO userDTO) {
        String companyName = requireText(userDTO.getCompanyName(), "Company name is required.");
        String username = requireText(userDTO.getUsername(), "Username is required.");
        String password = requireText(userDTO.getPassword(), "Password is required.");

        if (companyRepository.existsByNameIgnoreCase(companyName)) {
            throw new RuntimeException("A company with this name already exists.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists!");
        }

        Company company = companyRepository.save(new Company(companyName.trim()));

        User admin = new User(
                username.trim(),
                passwordEncoder.encode(password),
                "ROLE_ADMIN",
                company
        );
        admin.setEnabled(true);

        return userRepository.save(admin);
    }

    @Transactional
    public User createCompanyUser(UserDTO userDTO) {
        if (!CompanyContext.isAdmin()) {
            throw new RuntimeException("Only admins can create users for the company.");
        }

        String username = requireText(userDTO.getUsername(), "Username is required.");
        String password = requireText(userDTO.getPassword(), "Password is required.");
        String role = normalizeRole(userDTO.getRole());

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists!");
        }

        Long companyId = CompanyContext.requireCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found."));

        User user = new User(
                username.trim(),
                passwordEncoder.encode(password),
                role,
                company
        );
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public List<User> getUsersForCurrentCompany() {
        return userRepository.findByCompanyIdOrderByUsernameAsc(
                CompanyContext.requireCompanyId()
        );
    }

    public long countUsersForCurrentCompany() {
        return userRepository.countByCompanyId(CompanyContext.requireCompanyId());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getCompanyUser(Long userId) {
        return userRepository.findByIdAndCompanyId(userId, CompanyContext.requireCompanyId())
                .orElseThrow(() -> new RuntimeException("User not found in your company."));
    }

    /**
     * Public reset for company admins only. Verifies username + company name.
     */
    @Transactional
    public void resetCompanyAdminPassword(UserDTO userDTO) {
        String companyName = requireText(userDTO.getCompanyName(), "Company name is required.");
        String username = requireText(userDTO.getUsername(), "Username is required.");
        String password = requireText(userDTO.getPassword(), "New password is required.");
        requireMatchingPasswords(password, userDTO.getConfirmPassword());

        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new RuntimeException("No admin account matches that company and username."));

        if (!"ROLE_ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Forgot password is only available for company admins. Ask an admin to reset your password.");
        }

        if (user.getCompany() == null
                || !companyName.trim().equalsIgnoreCase(user.getCompany().getName())) {
            throw new RuntimeException("No admin account matches that company and username.");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("This account is disabled. Contact support or another admin.");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    /**
     * Admin resets another company user's password (cannot use this for own account).
     */
    @Transactional
    public void changePasswordForCompanyUser(Long userId, String newPassword, String confirmPassword) {
        if (!CompanyContext.isAdmin()) {
            throw new RuntimeException("Only admins can change passwords for company users.");
        }

        String password = requireText(newPassword, "New password is required.");
        requireMatchingPasswords(password, confirmPassword);

        Long currentUserId = CompanyContext.requireCurrentUser().getUserId();
        if (currentUserId.equals(userId)) {
            throw new RuntimeException("Use Forgot Password on the sign-in page to reset your own admin password.");
        }

        User user = getCompanyUser(userId);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private void requireMatchingPasswords(String password, String confirmPassword) {
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match.");
        }
        if (password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters.");
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "ROLE_STAFF";
        }

        String normalized = role.trim().toUpperCase();
        if ("ADMIN".equals(normalized) || "ROLE_ADMIN".equals(normalized)) {
            return "ROLE_ADMIN";
        }
        if ("STAFF".equals(normalized) || "ROLE_STAFF".equals(normalized)) {
            return "ROLE_STAFF";
        }

        throw new RuntimeException("Role must be Admin or Staff.");
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new RuntimeException(message);
        }
        return value;
    }
}
