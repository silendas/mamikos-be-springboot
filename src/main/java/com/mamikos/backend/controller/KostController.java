package com.mamikos.backend.controller;

import com.mamikos.backend.common.BaseResponse;
import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.KostRequest;
import com.mamikos.backend.dto.KostResponse;
import com.mamikos.backend.service.KostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/kosts")
@RequiredArgsConstructor
public class KostController {

    private final KostService kostService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BaseResponse<KostResponse>> createKost(
            Principal principal,
            @Valid @RequestBody KostRequest request) {
        KostResponse response = kostService.createKost(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(ResponseMessage.KOST_CREATED, response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BaseResponse<KostResponse>> updateKost(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody KostRequest request) {
        KostResponse response = kostService.updateKost(principal.getName(), id, request);
        return ResponseEntity.ok(BaseResponse.success(ResponseMessage.KOST_UPDATED, response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BaseResponse<Object>> deleteKost(
            Principal principal,
            @PathVariable Long id) {
        kostService.deleteKost(principal.getName(), id);
        return ResponseEntity.ok(BaseResponse.success(ResponseMessage.KOST_DELETED, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<KostResponse>> getKostById(@PathVariable Long id) {
        KostResponse response = kostService.getKostById(id);
        return ResponseEntity.ok(BaseResponse.success(ResponseMessage.KOST_FETCHED, response));
    }

    @GetMapping("/owner/my-kosts")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BaseResponse<List<KostResponse>>> getOwnerKosts(Principal principal) {
        List<KostResponse> response = kostService.getOwnerKosts(principal.getName());
        return ResponseEntity.ok(BaseResponse.success(ResponseMessage.KOST_FETCHED, response));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<KostResponse>>> searchKosts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sort) {
        List<KostResponse> response = kostService.searchKosts(name, location, minPrice, maxPrice, sort);
        return ResponseEntity.ok(BaseResponse.success(ResponseMessage.KOST_FETCHED, response));
    }
}
