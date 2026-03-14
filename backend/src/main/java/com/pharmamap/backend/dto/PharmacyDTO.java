package com.pharmamap.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Pharmacy instances, primarily used in Admin views.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyDTO {
    private Long id;
    private String name;
    private String ownerName;
    private String ownerEmail;
    private String licenseNumber;
    private String address;
    private String city;
    private String pincode;
    private String phone;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
