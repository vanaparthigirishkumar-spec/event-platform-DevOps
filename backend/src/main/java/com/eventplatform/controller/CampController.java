package com.eventplatform.controller;

import com.eventplatform.dto.camp.CampCreateRequest;
import com.eventplatform.dto.camp.CampResponse;
import com.eventplatform.dto.camp.CampUpdateRequest;
import com.eventplatform.dto.common.PageResponse;
import com.eventplatform.model.enums.CampType;
import com.eventplatform.service.CampService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/camps")
@RequiredArgsConstructor
public class CampController {

    private final CampService campService;

    @PostMapping
    public ResponseEntity<CampResponse> create(@Valid @RequestBody CampCreateRequest request,
                                                Authentication authentication) {
        CampResponse response = campService.create(request, authentication);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<CampResponse>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) CampType type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean published) {
        PageResponse<CampResponse> response = campService.list(page, size, sortBy, sortOrder, type, search, published);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<CampResponse>> getMyCamps(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            Authentication authentication) {
        PageResponse<CampResponse> response = campService.getMyCamps(page, size, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampResponse> getById(@PathVariable String id) {
        CampResponse response = campService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampResponse> update(@PathVariable String id,
                                                @Valid @RequestBody CampUpdateRequest request,
                                                Authentication authentication) {
        CampResponse response = campService.update(id, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        campService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }

}