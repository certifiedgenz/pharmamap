package com.pharmamap.backend.entity;

/**
 * Roles available for a user in the PharmaMap platform.
 */
public enum Role {
    CUSTOMER,    // General public searching for medicines
    PHARMACY,    // Pharmacy owner managing inventory
    ADMIN        // Platform administrator
}
