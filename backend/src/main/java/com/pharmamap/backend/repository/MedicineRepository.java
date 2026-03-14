package com.pharmamap.backend.repository;

import com.pharmamap.backend.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Medicine entity.
 */
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // General search matching full name (e.g. "Dolo 650 Tablet")
    List<Medicine> findByNameContainingIgnoreCase(String name);

    // Search by salt name (e.g. "Paracetamol")
    List<Medicine> findBySaltNameContainingIgnoreCase(String saltName);
    
    // Search by brand name (e.g. "Dolo")
    List<Medicine> findByBrandNameContainingIgnoreCase(String brandName);
}
