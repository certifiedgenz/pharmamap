package com.pharmamap.backend.repository;

import com.pharmamap.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Inventory entity.
 * Handles the Many-to-Many bridge data between Pharmacies and Medicines.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Get all inventory stock for a specific pharmacy
    List<Inventory> findByPharmacyId(Long pharmacyId);

    // Find all pharmacies that stock a specific medicine (where quantity > 0)
    List<Inventory> findByMedicineIdAndQuantityGreaterThan(Long medicineId, Integer quantity);

    // Check if a specific medicine exists in a specific pharmacy's inventory
    Optional<Inventory> findByPharmacyIdAndMedicineId(Long pharmacyId, Long medicineId);
    
    /**
     * Advanced custom query to find inventory by medicine name across all pharmacies.
     * Uses JOIN to search the medicine table's name column.
     */
    @Query("SELECT i FROM Inventory i JOIN i.medicine m " +
           "WHERE (LOWER(m.name) LIKE LOWER(CONCAT('%', :medicineName, '%')) " +
           "   OR LOWER(m.brandName) LIKE LOWER(CONCAT('%', :medicineName, '%')) " +
           "   OR LOWER(m.saltName) LIKE LOWER(CONCAT('%', :medicineName, '%'))) " +
           "AND i.quantity > 0")
    List<Inventory> findAvailableByMedicineName(@Param("medicineName") String medicineName);

    /**
     * Native SQL Query finding nearby pharmacies carrying a specific medicine using the Haversine formula.
     * 6371 is the Earth's radius in kilometers.
     * Only returns results within the specified radius threshold, sorted closest first.
     */
    @Query(value = "SELECT " +
            "i.price AS price, i.quantity AS quantity, " +
            "m.name AS medicineName, m.strength AS strength, m.form AS form, " +
            "p.id AS pharmacyId, p.name AS pharmacyName, p.address AS address, p.phone AS phone, " +
            "p.latitude AS latitude, p.longitude AS longitude, " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(p.latitude)) * " +
            "cos(radians(p.longitude) - radians(:userLng)) + " +
            "sin(radians(:userLat)) * sin(radians(p.latitude)))) AS distance " +
            "FROM inventory i " +
            "JOIN medicines m ON i.medicine_id = m.id " +
            "JOIN pharmacies p ON i.pharmacy_id = p.id " +
            "WHERE (LOWER(m.name) LIKE LOWER(CONCAT('%', :medicineName, '%')) " +
            "   OR LOWER(m.brand_name) LIKE LOWER(CONCAT('%', :medicineName, '%')) " +
            "   OR LOWER(m.salt_name) LIKE LOWER(CONCAT('%', :medicineName, '%')) " +
            "   OR SOUNDEX(m.name) = SOUNDEX(:medicineName) " +
            "   OR SOUNDEX(m.brand_name) = SOUNDEX(:medicineName)) " +
            "AND i.quantity > 0 " +
            "HAVING distance < :radiusKm " +
            "ORDER BY distance ASC", nativeQuery = true)
    List<Object[]> findNearbyMedicines(
            @Param("medicineName") String medicineName,
            @Param("userLat") Double userLat,
            @Param("userLng") Double userLng,
            @Param("radiusKm") Double radiusKm
    );
}
