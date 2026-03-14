package com.pharmamap.backend.controller;

import com.pharmamap.backend.dto.ApiResponse;
import com.pharmamap.backend.dto.InventoryRequest;
import com.pharmamap.backend.dto.InventoryResponse;
import com.pharmamap.backend.entity.Inventory;
import com.pharmamap.backend.entity.Pharmacy;
import com.pharmamap.backend.entity.User;
import com.pharmamap.backend.repository.UserRepository;
import com.pharmamap.backend.service.InventoryService;
import com.pharmamap.backend.service.PharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * Secure REST APIs for Pharmacy Owners to manage their local stock.
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final PharmacyService pharmacyService;
    private final UserRepository userRepository;

    /**
     * Helper mapping method to convert the Inventory JPA entity to a clean DTO.
     */
    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .inventoryId(inventory.getId())
                .medicineId(inventory.getMedicine().getId())
                .medicineName(inventory.getMedicine().getName())
                .brandName(inventory.getMedicine().getBrandName())
                .strength(inventory.getMedicine().getStrength())
                .form(inventory.getMedicine().getForm())
                .quantity(inventory.getQuantity())
                .price(inventory.getPrice())
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }

    /**
     * Retrieves the Pharmacy ID dynamically based on the logged-in JWT user.
     * Prevents a Pharmacy from editing another pharmacy's stock.
     */
    private Long getAuthenticatedPharmacyId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        Pharmacy pharmacy = pharmacyService.getPharmacyByOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Pharmacy profile not found for this user."));

        return pharmacy.getId();
    }

    /**
     * GET /api/inventory
     * Lists the authenticated pharmacy's entire stock.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getMyInventory(Authentication authentication) {
        Long pharmacyId = getAuthenticatedPharmacyId(authentication);
        
        List<InventoryResponse> inventoryList = inventoryService.getInventoryByPharmacy(pharmacyId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(ApiResponse.success(inventoryList));
    }

    /**
     * POST /api/inventory
     * Add a new medicine to the local pharmacy stock.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> addMedicineToInventory(
            @Valid @RequestBody InventoryRequest request,
            Authentication authentication
    ) {
        Long pharmacyId = getAuthenticatedPharmacyId(authentication);
        
        Inventory savedStock = inventoryService.addOrUpdateStock(
                pharmacyId, 
                request.getMedicineId(), 
                request.getQuantity(), 
                request.getPrice()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(mapToResponse(savedStock)));
    }

    /**
     * PUT /api/inventory/{id}
     * Update the quantity and price of an existing stock record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequest request,
            Authentication authentication
    ) {
        Long pharmacyId = getAuthenticatedPharmacyId(authentication);
        
        Inventory updatedStock = inventoryService.addOrUpdateStock(
                pharmacyId, 
                request.getMedicineId(), 
                request.getQuantity(), 
                request.getPrice()
        );

        return ResponseEntity.ok(ApiResponse.success(mapToResponse(updatedStock)));
    }

    /**
     * DELETE /api/inventory/{id}
     * Remove an item from the stock listing entirely.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeMedicine(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long pharmacyId = getAuthenticatedPharmacyId(authentication);
        
        // Authorization check happens securely inside the Service layer
        inventoryService.deleteStockItem(id, pharmacyId);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * POST /api/inventory/upload
     * Bulk upload inventory items via CSV file
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadCsv(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        Long pharmacyId = getAuthenticatedPharmacyId(authentication);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please select a file to upload."));
        }

        try {
            Map<String, Object> results = inventoryService.uploadInventoryCsv(pharmacyId, file);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Could not process the file: " + e.getMessage()));
        }
    }
}
