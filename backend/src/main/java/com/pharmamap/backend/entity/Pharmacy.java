package com.pharmamap.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a registered Pharmacy.
 * Each pharmacy is owned by exactly one User (owner_id).
 */
@Entity
@Table(name = "pharmacies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 1 User can own 1 Pharmacy in this design (One-To-One relationship)
    // FetchType.LAZY ensures the User is only loaded from DB when explicitly requested
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    // TEXT type to hold long addresses
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 20)
    private String pincode;

    // Storing Geographic coordinates to allow proximity searches (e.g. within 5 km)
    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 20)
    private String phone;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
