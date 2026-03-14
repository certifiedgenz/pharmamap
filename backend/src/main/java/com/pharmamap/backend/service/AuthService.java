package com.pharmamap.backend.service;

import com.pharmamap.backend.dto.AuthRequest;
import com.pharmamap.backend.dto.AuthResponse;
import com.pharmamap.backend.dto.RegisterRequest;
import com.pharmamap.backend.entity.User;
import com.pharmamap.backend.entity.Pharmacy;
import com.pharmamap.backend.entity.Role;
import com.pharmamap.backend.repository.UserRepository;
import com.pharmamap.backend.repository.PharmacyRepository;
import com.pharmamap.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service orchestrating user login, registration, and JWT creation.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Handles new user registration.
     */
    public AuthResponse register(RegisterRequest request) {
        
        // Prevent duplicate emails
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        // Create new User entity mapping the request details
        User user = new User();
        user.setName(request.getName() != null ? request.getName() : "Owner");
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        
        // Hash the password securely!
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Save to Database
        userRepository.save(user);

        if (Role.PHARMACY.equals(request.getRole())) {
            Pharmacy pharmacy = new Pharmacy();
            pharmacy.setName(request.getPharmacyName());
            pharmacy.setOwner(user);
            pharmacy.setLicenseNumber(request.getLicenseNumber());
            pharmacy.setAddress(request.getAddress());
            pharmacy.setCity(request.getCity());
            pharmacy.setPincode(request.getPincode());
            pharmacy.setLatitude(request.getLatitude());
            pharmacy.setLongitude(request.getLongitude());
            pharmacy.setPhone(request.getPhone());
            pharmacyRepository.save(pharmacy);
        }

        // Convert entity to Spring Security UserDetails representation
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );

        // Generate Token immediately after signing up
        var jwtToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Verifies credentials and generates a token for existing users.
     */
    public AuthResponse authenticate(AuthRequest request) {
        
        // The AuthenticationManager explicitly validates the supplied email + password against the database (via CustomUserDetailsService)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If it got here without throwing bad credentials exception, user is valid.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );

        // Generate a fresh session token
        var jwtToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
