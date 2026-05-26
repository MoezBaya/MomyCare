package com.example.MomyCare.controller;

import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import com.example.MomyCare.service.OrdonnanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/consultations/{consultationId}/ordonnances")
@RequiredArgsConstructor
public class OrdonnanceController {

    private final OrdonnanceService ordonnanceService;

    @PostMapping
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<OrdonnanceResponseDTO> createOrdonnance(
            @PathVariable Long consultationId,
            @Valid @RequestBody OrdonnanceRequestDTO dto,
            Authentication auth) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ordonnanceService.createOrdonnance(consultationId, dto, auth));
    }

    @GetMapping("/{ordonnanceId}")
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENT')")
    public ResponseEntity<OrdonnanceResponseDTO> getOrdonnance(
            @PathVariable Long consultationId,
            @PathVariable Long ordonnanceId) {

        return ResponseEntity.ok(
                ordonnanceService.getOrdonnanceById(consultationId, ordonnanceId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENT')")
    public ResponseEntity<List<OrdonnanceResponseDTO>> getOrdonnancesByConsultation(
            @PathVariable Long consultationId) {

        return ResponseEntity.ok(
                ordonnanceService.getOrdonnancesByConsultation(consultationId));
    }

    @DeleteMapping("/{ordonnanceId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<Void> deleteOrdonnance(
            @PathVariable Long consultationId,
            @PathVariable Long ordonnanceId,
            Authentication auth) {

        ordonnanceService.deleteOrdonnance(consultationId, ordonnanceId, auth);
        return ResponseEntity.noContent().build();
    }
}