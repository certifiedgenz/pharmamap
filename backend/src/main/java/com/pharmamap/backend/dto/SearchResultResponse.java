package com.pharmamap.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Clean data format sent back for public medicine searches.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultResponse {

    // Medicine Details
    private String medicineName;
    private String strength;
    private String form;
    
    // Pharmacy Details
    private Long pharmacyId;
    private String pharmacyName;
    private String address;
    private String phone;
    
    // Location and Distance
    private Double latitude;
    private Double longitude;
    private Double distanceInKm; // The calculated geographic distance!
    
    // Stock data
    private Integer quantityAvailable;
    private BigDecimal price;
}
