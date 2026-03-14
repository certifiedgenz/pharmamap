package com.pharmamap.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The association table bridging Pharmacy and Medicine.
 * Acts as the Inventory / Stock for a specific pharmacy indicating if they hold a given medicine.
 */
@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_medicine", columnList = "medicine_id"),
    @Index(name = "idx_inventory_pharmacy", columnList = "pharmacy_id"),
    @Index(name = "idx_inventory_quantity", columnList = "quantity")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many inventory records belong to One Pharmacy.
    // FetchType.LAZY avoids retrieving the entire Pharmacy model when we just need the stock.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    // Many inventory records belong to One Master Medicine.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    // Available count in the pharmacy. Defaults to out-of-stock (0)
    @Column(nullable = false)
    private Integer quantity = 0;

    // Selling price for the medicine as defined by this particular pharmacy
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
