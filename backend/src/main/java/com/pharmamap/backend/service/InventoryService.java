package com.pharmamap.backend.service;

import com.pharmamap.backend.entity.Inventory;
import com.pharmamap.backend.entity.Medicine;
import com.pharmamap.backend.entity.Pharmacy;
import com.pharmamap.backend.repository.InventoryRepository;
import com.pharmamap.backend.repository.MedicineRepository;
import com.pharmamap.backend.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service handling Inventory stock updates and searches.
 * This acts as the bridge coordinating Pharmacies and Medicines.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;

    /**
     * Let a pharmacy owner view their entire stock list.
     */
    public List<Inventory> getInventoryByPharmacy(Long pharmacyId) {
        return inventoryRepository.findByPharmacyId(pharmacyId);
    }

    /**
     * Let a customer search for pharmacies that have a specific medicine in stock.
     */
    public List<Inventory> findAvailableStockForMedicine(Long medicineId) {
        // We only want pharmacies where quantity > 0
        return inventoryRepository.findByMedicineIdAndQuantityGreaterThan(medicineId, 0);
    }

    /**
     * Advance customer search by medicine name directly using the custom @Query.
     */
    public List<Inventory> searchAvailableStockByMedicineName(String medicineName) {
        return inventoryRepository.findAvailableByMedicineName(medicineName);
    }

    /**
     * Allow a Pharmacy Owner to update the stock for a specific medicine.
     * If the inventory record exists, it updates it. If not, it creates a new one.
     */
    public Inventory addOrUpdateStock(Long pharmacyId, Long medicineId, Integer quantity, java.math.BigDecimal price) {
        
        // Ensure both Pharmacy and Medicine exist before creating stock
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));
                
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        // Check if the pharmacy already has a stock record for this medicine
        Optional<Inventory> existingStock = inventoryRepository.findByPharmacyIdAndMedicineId(pharmacyId, medicineId);

        Inventory inventoryToSave;

        if (existingStock.isPresent()) {
            // Update existing stock
            inventoryToSave = existingStock.get();
            inventoryToSave.setQuantity(quantity);
            inventoryToSave.setPrice(price);
        } else {
            // Create brand new stock entry
            inventoryToSave = new Inventory();
            inventoryToSave.setPharmacy(pharmacy);
            inventoryToSave.setMedicine(medicine);
            inventoryToSave.setQuantity(quantity);
            inventoryToSave.setPrice(price);
        }

        return inventoryRepository.save(inventoryToSave);
    }

    /**
     * Delete an inventory record entirely, ensuring the requesting pharmacy owns it.
     */
    public void deleteStockItem(Long inventoryId, Long requestingPharmacyId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Stock item not found"));

        if (!inventory.getPharmacy().getId().equals(requestingPharmacyId)) {
            throw new RuntimeException("Unauthorized: You do not own this stock record.");
        }

        inventoryRepository.deleteById(inventoryId);
    }

    /**
     * Process a CSV file containing (MedicineName, Quantity, Price)
     * Returns a map containing the results (e.g., success count, error count).
     */
    public Map<String, Object> uploadInventoryCsv(Long pharmacyId, MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int notFoundCount = 0;
        int errorCount = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            boolean isFirstRow = true;

            while ((line = reader.readNext()) != null) {
                // Skip header row
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                if (line.length < 3) {
                    errorCount++;
                    continue;
                }

                String medName = line[0].trim();
                
                try {
                    Integer quantity = Integer.parseInt(line[1].trim());
                    BigDecimal price = new BigDecimal(line[2].trim());

                    // Find the medicine by name
                    List<Medicine> matches = medicineRepository.findByNameContainingIgnoreCase(medName);
                    
                    if (!matches.isEmpty()) {
                        // Take the first best match
                        Medicine matchedMedicine = matches.get(0);
                        addOrUpdateStock(pharmacyId, matchedMedicine.getId(), quantity, price);
                        successCount++;
                    } else {
                        notFoundCount++;
                    }
                } catch (NumberFormatException e) {
                    errorCount++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }

        result.put("processed", successCount);
        result.put("medicinesNotFound", notFoundCount);
        result.put("formatErrors", errorCount);
        return result;
    }
}
