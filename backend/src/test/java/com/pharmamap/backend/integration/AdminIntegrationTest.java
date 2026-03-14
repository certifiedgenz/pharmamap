package com.pharmamap.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmamap.backend.dto.AuthRequest;
import com.pharmamap.backend.dto.RegisterRequest;
import com.pharmamap.backend.entity.Pharmacy;
import com.pharmamap.backend.entity.Role;
import com.pharmamap.backend.repository.PharmacyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    private String adminJwtToken;
    private String customerJwtToken;
    private Long testPharmacyId;

    @BeforeEach
    public void setup() throws Exception {
        // Register an Admin User
        RegisterRequest adminRegister = RegisterRequest.builder()
                .name("Root Admin")
                .email("admin@system.com")
                .password("adminpass")
                .role(Role.ADMIN)
                .build();
        mockMvc.perform(postJson("/api/auth/register", adminRegister)).andExpect(status().isOk());

        // Register a Customer User
        RegisterRequest customerRegister = RegisterRequest.builder()
                .name("Standard Customer")
                .email("cust@system.com")
                .password("custpass")
                .role(Role.CUSTOMER)
                .build();
        mockMvc.perform(postJson("/api/auth/register", customerRegister)).andExpect(status().isOk());

        // Register a Pharmacy User (which generates a Pharmacy entity)
        RegisterRequest pharmRegister = RegisterRequest.builder()
                .name("Test Pharm")
                .email("testpharm@system.com")
                .password("pharmpass")
                .role(Role.PHARMACY)
                .pharmacyName("Test Pharmacy Hub")
                .licenseNumber("PHARM123")
                .address("123 Street")
                .city("New York")
                .pincode("10001")
                .phone("5551234567")
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();
        mockMvc.perform(postJson("/api/auth/register", pharmRegister)).andExpect(status().isOk());

        // Extract Tokens
        adminJwtToken = extractToken(new AuthRequest("admin@system.com", "adminpass"));
        customerJwtToken = extractToken(new AuthRequest("cust@system.com", "custpass"));

        // Retrieve pharmacy ID generated natively
        Pharmacy p = pharmacyRepository.findByNameContainingIgnoreCase("Test Pharmacy Hub").get(0);
        testPharmacyId = p.getId();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder postJson(String url, Object body) throws Exception {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
    }

    private String extractToken(AuthRequest authRequest) throws Exception {
        MvcResult res = mockMvc.perform(postJson("/api/auth/login", authRequest)).andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString()).get("data").get("token").asText();
    }

    @Test
    public void testAdminAccessRestricted() throws Exception {
        // Customer hitting admin endpoint should receive 403 Forbidden
        mockMvc.perform(get("/api/admin/pharmacies")
                .header("Authorization", "Bearer " + customerJwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAdminGetPharmacies() throws Exception {
        mockMvc.perform(get("/api/admin/pharmacies")
                .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").exists());
    }

    @Test
    public void testApprovePharmacy() throws Exception {
        mockMvc.perform(put("/api/admin/pharmacies/" + testPharmacyId + "/approve")
                .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isVerified").value(true));
    }

    @Test
    public void testTogglePharmacyStatus() throws Exception {
        mockMvc.perform(put("/api/admin/pharmacies/" + testPharmacyId + "/toggle-active")
                .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isActive").value(false)); // Should toggle from true -> false
    }

    @Test
    public void testGetPlatformStatistics() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                .header("Authorization", "Bearer " + adminJwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").isNumber())
                .andExpect(jsonPath("$.data.totalPharmacies").isNumber())
                .andExpect(jsonPath("$.data.activePharmacies").isNumber());
    }
}
