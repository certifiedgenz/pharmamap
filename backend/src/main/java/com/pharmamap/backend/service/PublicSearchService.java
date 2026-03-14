package com.pharmamap.backend.service;

import com.pharmamap.backend.dto.SearchResultResponse;
import com.pharmamap.backend.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service specifically for parsing raw Object[] native queries into our Search DTOs.
 */
@Service
@RequiredArgsConstructor
public class PublicSearchService {

    private final InventoryRepository inventoryRepository;

    /**
     * Executes the Haversine query and converts the raw ResultSet natively into clean DTOs.
     */
    public List<SearchResultResponse> searchNearbyMedicines(String medicineName, Double lat, Double lng) {
        // We'll hardcode a 15km default radius for the search for now, could be passed as a parameter later.
        Double defaultRadiusKm = 15.0;
        
        List<Object[]> rawResults = inventoryRepository.findNearbyMedicines(medicineName, lat, lng, defaultRadiusKm);
        List<SearchResultResponse> parsedResults = new ArrayList<>();

        for (Object[] row : rawResults) {
            SearchResultResponse dto = SearchResultResponse.builder()
                    .price(new BigDecimal(row[0].toString()))
                    .quantityAvailable(((Number) row[1]).intValue())
                    .medicineName((String) row[2])
                    .strength((String) row[3])
                    .form((String) row[4])
                    .pharmacyId(((Number) row[5]).longValue())
                    .pharmacyName((String) row[6])
                    .address((String) row[7])
                    .phone((String) row[8])
                    .latitude(row[9] != null ? ((Number) row[9]).doubleValue() : null)
                    .longitude(row[10] != null ? ((Number) row[10]).doubleValue() : null)
                    .distanceInKm(((Number) row[11]).doubleValue())
                    .build();
                    
            parsedResults.add(dto);
        }

        return parsedResults;
    }
}
