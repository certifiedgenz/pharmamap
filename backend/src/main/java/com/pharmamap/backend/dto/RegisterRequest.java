package com.pharmamap.backend.dto;

import com.pharmamap.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for User Registration.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private Role role;
    
    // Pharmacy specific fields
    private String pharmacyName;
    private String licenseNumber;
    private String address;
    private String city;
    private String pincode;
    private Double latitude;
    private Double longitude;
}
