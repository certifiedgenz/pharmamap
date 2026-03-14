package com.pharmamap.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmamap.backend.dto.InventoryRequest;
import com.pharmamap.backend.dto.AuthRequest;
import com.pharmamap.backend.dto.RegisterRequest;
import com.pharmamap.backend.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    public void setup() throws Exception {
        // 1. Register a Pharmacy User first so we can obtain a token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("InterPharmacy")
                .email("inter@pharmacy.com")
                .password("secure123")
                .role(Role.PHARMACY)
                .phone("0987654321")
                .pharmacyName("InterPharmacy")
                .address("123 Test St")
                .city("TestCity")
                .pincode("12345")
                .licenseNumber("LIC12345")
                .latitude(10.0)
                .longitude(20.0)
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. Login to get JWT Token
        AuthRequest loginRequest = AuthRequest.builder()
                .email("inter@pharmacy.com")
                .password("secure123")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // 3. Extract token from response to inject into headers later
        String responseBody = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(responseBody).get("data").get("token").asText();
    }

    @Test
    public void testAddAndGetInventory() throws Exception {
        // We assume medicine ID 1 exists from data.sql
        InventoryRequest addRequest = new InventoryRequest();
        addRequest.setMedicineId(1L);
        addRequest.setQuantity(100);
        addRequest.setPrice(new BigDecimal("15.50"));

        // Add
        mockMvc.perform(post("/api/inventory")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantity").value(100))
                .andExpect(jsonPath("$.data.price").value(15.5));

        // Get to verify
        mockMvc.perform(get("/api/inventory")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quantity").value(100))
                .andExpect(jsonPath("$.data[0].price").value(15.5));
    }

    @Test
    public void testUpdateInventory() throws Exception {
        // Target add request first
        InventoryRequest addRequest = new InventoryRequest();
        addRequest.setMedicineId(1L);
        addRequest.setQuantity(50);
        addRequest.setPrice(new BigDecimal("10.00"));

        MvcResult addResult = mockMvc.perform(post("/api/inventory")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract native inventory ID
        String addResponse = addResult.getResponse().getContentAsString();
        Long inventoryId = objectMapper.readTree(addResponse).get("data").get("inventoryId").asLong();

        // Update item
        InventoryRequest updateRequest = new InventoryRequest();
        updateRequest.setMedicineId(1L);
        updateRequest.setQuantity(75);
        updateRequest.setPrice(new BigDecimal("12.00"));

        mockMvc.perform(put("/api/inventory/" + inventoryId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(75))
                .andExpect(jsonPath("$.data.price").value(12.0));
    }

    @Test
    public void testUploadInventoryCsv() throws Exception {
        // Prepare a mock CSV content: MedicineName,Quantity,Price
        // Ensure "Paracetamol" exists in data.sql for the test to match
        String csvContent = "Medicine Name,Quantity,Price\n" +
                            "Paracetamol,100,5.99\n" +
                            "NonExistentMedicine123,50,15.50\n" +
                            "BadRowNoPrice,10\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",                 // Parameter name expected by the controller
                "inventory.csv",        // Original file name
                "text/csv",             // Content type
                csvContent.getBytes()   // File content
        );

        mockMvc.perform(multipart("/api/inventory/upload")
                .file(file)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.formatErrors").value(1)); // The bad row
    }
}
