package com.mamikos.backend.service;

import com.mamikos.backend.dto.KostRequest;
import com.mamikos.backend.dto.KostResponse;
import com.mamikos.backend.dto.PageResponse;

import java.util.List;

public interface KostService {
    KostResponse createKost(String username, KostRequest request);
    KostResponse updateKost(String username, Long kostId, KostRequest request);
    void deleteKost(String username, Long kostId);
    KostResponse getKostById(Long kostId);
    List<KostResponse> getOwnerKosts(String username);
    PageResponse<KostResponse> searchKosts(String name, String location, Double minPrice, Double maxPrice, String sortDirection, int page, int size);
}

