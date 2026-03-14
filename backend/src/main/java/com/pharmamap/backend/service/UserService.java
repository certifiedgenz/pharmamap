package com.pharmamap.backend.service;

import com.pharmamap.backend.entity.User;
import com.pharmamap.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service handling User business logic.
 * Uses constructor injection via Lombok's @RequiredArgsConstructor.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retrieve a user by their ID.
     * @param id The ID of the user.
     * @return Optional containing the User if found, empty otherwise.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieve a user by their email address.
     * @param email The email address to search for.
     * @return Optional containing the User if found, empty otherwise.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Store a new user in the database.
     * @param user The User entity to save.
     * @return The saved User entity.
     */
    public User saveUser(User user) {
        // Here we would normally hash the password before saving!
        return userRepository.save(user);
    }
}
