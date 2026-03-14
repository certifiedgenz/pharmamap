package com.pharmamap.backend.repository;

import com.pharmamap.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides basic CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Useful for finding a user during login
    Optional<User> findByEmail(String email);
    
    // Check if an email is already registered during signup
    boolean existsByEmail(String email);
}
