package com.pharmamap.backend.controller;

import com.pharmamap.backend.dto.ApiResponse;
import com.pharmamap.backend.dto.SearchResultResponse;
import com.pharmamap.backend.service.PublicSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public REST APIs open to Customers without needing JWT Authentication.
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allows access from frontend applications
public class PublicSearchController {

    private final PublicSearchService publicSearchService;

    /**
     * GET /api/search?medicine=paracetamol&lat=19.0760&lng=72.8777
     * 
     * Performs a geographic radius search returning pharmacies carrying the medicine.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SearchResultResponse>>> searchMedicine(
            @RequestParam(name = "medicine") String medicineName,
            @RequestParam(name = "lat") Double lat,
            @RequestParam(name = "lng") Double lng
    ) {
        // Using the service to run our native SQL Haversine query!
        List<SearchResultResponse> results = publicSearchService.searchNearbyMedicines(medicineName, lat, lng);
        
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
