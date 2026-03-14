package com.pharmamap.backend.controller;

import com.pharmamap.backend.dto.ApiResponse;
import com.pharmamap.backend.dto.PharmacyDTO;
import com.pharmamap.backend.entity.Pharmacy;
import com.pharmamap.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller strictly dedicated for Administrative endpoints.
 * Requires ADMIN Role to execute any method.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Get a comprehensive array of all Pharmacies registered.
     */
    @GetMapping("/pharmacies")
    public ResponseEntity<ApiResponse<List<PharmacyDTO>>> getAllPharmacies() {
        List<PharmacyDTO> pharmacies = adminService.getAllPharmacies();
        return ResponseEntity.ok(ApiResponse.success(pharmacies, "Pharmacies retrieved successfully."));
    }

    /**
     * Mark a pharmacy as legally verified and permitted to operate.
     */
    @PutMapping("/pharmacies/{id}/approve")
    public ResponseEntity<ApiResponse<Pharmacy>> approvePharmacy(@PathVariable Long id) {
        try {
            Pharmacy approved = adminService.approvePharmacy(id);
            return ResponseEntity.ok(ApiResponse.success(approved, "Pharmacy verified successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to verify pharmacy: " + e.getMessage()));
        }
    }

    /**
     * Disable or Enable a pharmacy's platform access without destroying its inventory DB structure.
     */
    @PutMapping("/pharmacies/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Pharmacy>> togglePharmacyActiveStatus(@PathVariable Long id) {
        try {
            Pharmacy toggled = adminService.togglePharmacyActiveStatus(id);
            String statusDesc = toggled.getIsActive() != null && toggled.getIsActive() ? "activated" : "deactivated";
            return ResponseEntity.ok(ApiResponse.success(toggled, "Pharmacy " + statusDesc + " successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to toggle pharmacy status: " + e.getMessage()));
        }
    }

    /**
     * Retrieve aggregate analytics counters.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getPlatformStatistics() {
        Map<String, Long> stats = adminService.getPlatformStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats, "Platform statistics retrieved."));
    }
}
