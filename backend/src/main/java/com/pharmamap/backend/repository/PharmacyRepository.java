package com.pharmamap.backend.repository;

import com.pharmamap.backend.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Pharmacy entity.
 */
@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    // Find a pharmacy by its owner's user ID
    Optional<Pharmacy> findByOwnerId(Long ownerId);

    // Search for pharmacies by name (case-insensitive)
    List<Pharmacy> findByNameContainingIgnoreCase(String name);
    
    // Find all verified pharmacies in a specific city
    List<Pharmacy> findByCityAndIsVerifiedTrue(String city);
    
    // Find pharmacies by pincode
    List<Pharmacy> findByPincode(String pincode);
}
