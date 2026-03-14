package com.pharmamap.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Master catalog of a Medicine.
 */
@Entity
@Table(name = "medicines", indexes = {
    @Index(name = "idx_medicine_name", columnList = "name"),
    @Index(name = "idx_medicine_brand", columnList = "brand_name"),
    @Index(name = "idx_medicine_salt", columnList = "salt_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Full name, e.g. "Dolo 650 Tablet"
    @Column(nullable = false)
    private String name;

    // e.g. "Dolo 650"
    @Column(name = "brand_name")
    private String brandName;

    // Generic salt, e.g. "Paracetamol"
    @Column(name = "salt_name")
    private String saltName;

    // e.g. "650mg"
    @Column(length = 50)
    private String strength;

    // e.g. "Tablet"
    @Column(length = 50)
    private String form;

    private String manufacturer;

    // Is Doctor's prescription needed to buy?
    @Column(name = "prescription_required")
    private Boolean prescriptionRequired = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Override equals and hashCode for safe uses in Collections (Sets/Lists) based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        Medicine medicine = (Medicine) o;
        return id != null && id.equals(medicine.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
