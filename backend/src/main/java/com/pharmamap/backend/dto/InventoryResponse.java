package com.pharmamap.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clean data format sent back to the Pharmacy dashboard avoiding exposing internal objects like the whole Pharmacy entity.
 */
@Data
@Builder
public class InventoryResponse {
    
    // Internal inventory Row ID
    private Long inventoryId;
    
    // Core Medicine Details
    private Long medicineId;
    private String medicineName;
    private String brandName;
    private String strength;
    private String form;
    
    // Pharmacy specific pricing & count
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime lastUpdated;
}
