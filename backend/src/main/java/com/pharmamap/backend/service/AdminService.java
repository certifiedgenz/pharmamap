package com.pharmamap.backend.service;

import com.pharmamap.backend.dto.PharmacyDTO;
import com.pharmamap.backend.entity.Pharmacy;
import com.pharmamap.backend.repository.InventoryRepository;
import com.pharmamap.backend.repository.MedicineRepository;
import com.pharmamap.backend.repository.PharmacyRepository;
import com.pharmamap.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Retrieves all registered pharmacies mapped to DTO.
     */
    public List<PharmacyDTO> getAllPharmacies() {
        return pharmacyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Approves/verifies a pharmacy.
     */
    public Pharmacy approvePharmacy(Long pharmacyId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));
        
        pharmacy.setIsVerified(true);
        return pharmacyRepository.save(pharmacy);
    }

    /**
     * Toggles the active status of a pharmacy (soft activate/deactivate).
     */
    public Pharmacy togglePharmacyActiveStatus(Long pharmacyId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));
        
        pharmacy.setIsActive(!pharmacy.getIsActive());
        return pharmacyRepository.save(pharmacy);
    }

    /**
     * Returns platform statistics.
     */
    public Map<String, Long> getPlatformStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalPharmacies", pharmacyRepository.count());
        stats.put("totalMedicines", medicineRepository.count());
        stats.put("totalInventoryItems", inventoryRepository.count());
        
        // Add specific active/verified counts if desired
        long verifiedPharmacies = pharmacyRepository.findAll().stream().filter(Pharmacy::getIsVerified).count();
        long activePharmacies = pharmacyRepository.findAll().stream().filter(p -> p.getIsActive() != null && p.getIsActive()).count();
        stats.put("verifiedPharmacies", verifiedPharmacies);
        stats.put("activePharmacies", activePharmacies);
        
        return stats;
    }

    private PharmacyDTO mapToDTO(Pharmacy pharmacy) {
        return PharmacyDTO.builder()
                .id(pharmacy.getId())
                .name(pharmacy.getName())
                .ownerName(pharmacy.getOwner() != null ? pharmacy.getOwner().getName() : "Unknown")
                .ownerEmail(pharmacy.getOwner() != null ? pharmacy.getOwner().getEmail() : "Unknown")
                .licenseNumber(pharmacy.getLicenseNumber())
                .address(pharmacy.getAddress())
                .city(pharmacy.getCity())
                .pincode(pharmacy.getPincode())
                .phone(pharmacy.getPhone())
                .isVerified(pharmacy.getIsVerified())
                .isActive(pharmacy.getIsActive() != null ? pharmacy.getIsActive() : true)
                .createdAt(pharmacy.getCreatedAt())
                .build();
    }
}
