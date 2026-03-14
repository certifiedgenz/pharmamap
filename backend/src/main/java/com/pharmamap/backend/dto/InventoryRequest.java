package com.pharmamap.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object for inventory creation or updates.
 * Includes basic Bean Validations to ensure valid quantities and prices.
 */
@Data
public class InventoryRequest {

    // Identifies the master medicine in the catalog we are adding stock for
    @NotNull(message = "Medicine ID cannot be null")
    private Long medicineId;

    // Quantity must be 0 or higher (0 means out of stock)
    @NotNull(message = "Quantity must be provided")
    @Min(value = 0, message = "Quantity cannot be less than 0")
    private Integer quantity;

    // Price must be strictly positive
    @NotNull(message = "Price must be provided")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;
}
