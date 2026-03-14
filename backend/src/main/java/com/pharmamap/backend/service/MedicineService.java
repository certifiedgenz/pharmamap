package com.pharmamap.backend.service;

import com.pharmamap.backend.entity.Medicine;
import com.pharmamap.backend.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service handling the Master Medicine catalog business logic.
 */
@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    /**
     * Get a list of all master medicines available in the platform catalog.
     */
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    /**
     * Find a specific medicine by its catalog ID.
     */
    public Optional<Medicine> getMedicineById(Long id) {
        return medicineRepository.findById(id);
    }

    /**
     * Search the catalog for a medicine by its full name.
     */
    public List<Medicine> searchMedicineByName(String name) {
        return medicineRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Search the catalog for generic alternatives by salt name.
     */
    public List<Medicine> searchMedicineBySaltName(String saltName) {
        return medicineRepository.findBySaltNameContainingIgnoreCase(saltName);
    }

    /**
     * Add a new medicine to the master catalog.
     */
    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }
}
