package com.pharmamap.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmamap.backend.dto.AuthRequest;
import com.pharmamap.backend.dto.RegisterRequest;
import com.pharmamap.backend.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Will rollback any database inserts after each test so tests stay isolated!
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUserRegistrationAndLoginSuccess() throws Exception {
        // 1. Register a new user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Integration Test User")
                .email("integration@test.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .phone("1234567890")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 2. Login with the registered user
        AuthRequest loginRequest = AuthRequest.builder()
                .email("integration@test.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.email").value("integration@test.com"))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        AuthRequest loginRequest = AuthRequest.builder()
                .email("nonexistent@example.com")
                .password("wrongpassword")
                .build();

        // Should return 403 Forbidden for bad credentials in standard Spring Security
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUnauthorizedAccessToProtectedEndpoint() throws Exception {
        // Attempt to access restricted inventory endpoint without JWT token
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isForbidden());
    }
}
