package com.example.inventory_management.repository;

import com.example.inventory_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    long countByCompanyId(Long companyId);
    List<User> findByCompanyIdOrderByUsernameAsc(Long companyId);
    Optional<User> findByIdAndCompanyId(Long id, Long companyId);
}
