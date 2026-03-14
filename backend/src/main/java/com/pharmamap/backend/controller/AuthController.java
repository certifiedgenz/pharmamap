package com.pharmamap.backend.controller;

import com.pharmamap.backend.dto.ApiResponse;
import com.pharmamap.backend.dto.AuthRequest;
import com.pharmamap.backend.dto.AuthResponse;
import com.pharmamap.backend.dto.RegisterRequest;
import com.pharmamap.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST APIs exposed for public facing authentication calls.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for new user/pharmacy registration.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.register(request)));
    }

    /**
     * Endpoint for existing user/pharmacy login.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.authenticate(request)));
    }
}
