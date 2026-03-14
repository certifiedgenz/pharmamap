package com.pharmamap.backend.service;

import com.pharmamap.backend.entity.Pharmacy;
import com.pharmamap.backend.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service handling Pharmacy business logic.
 */
@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    /**
     * Get all pharmacies registered in the system.
     */
    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }

    /**
     * Find a single pharmacy by its unique ID.
     */
    public Optional<Pharmacy> getPharmacyById(Long id) {
        return pharmacyRepository.findById(id);
    }

    /**
     * Search for pharmacies based on a city and ensure they are verified by the Admin.
     */
    public List<Pharmacy> getVerifiedPharmaciesByCity(String city) {
        return pharmacyRepository.findByCityAndIsVerifiedTrue(city);
    }

    /**
     * Retrieve the pharmacy profile associated with a specific owner (User ID).
     */
    public Optional<Pharmacy> getPharmacyByOwnerId(Long ownerId) {
        return pharmacyRepository.findByOwnerId(ownerId);
    }

    /**
     * Add a new pharmacy or update an existing one.
     */
    public Pharmacy savePharmacy(Pharmacy pharmacy) {
        return pharmacyRepository.save(pharmacy);
    }

    /**
     * Admin functionality to flip the verification status of a pharmacy.
     */
    public void verifyPharmacy(Long pharmacyId) {
        Optional<Pharmacy> pharmacyOpt = pharmacyRepository.findById(pharmacyId);
        if (pharmacyOpt.isPresent()) {
            Pharmacy pharmacy = pharmacyOpt.get();
            pharmacy.setIsVerified(true);
            pharmacyRepository.save(pharmacy);
        }
    }
}
