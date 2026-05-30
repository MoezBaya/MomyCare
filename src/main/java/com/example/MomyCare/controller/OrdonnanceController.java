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

@RestController
@RequestMapping("/api/consultations/{consultationId}/ordonnances")
@RequiredArgsConstructor
public class OrdonnanceController {

    private final OrdonnanceService ordonnanceService;

    // POST — créer une ordonnance
    @PostMapping
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<OrdonnanceResponseDTO> createOrdonnance(
            Authentication auth,
            @PathVariable Long consultationId,
            @Valid @RequestBody OrdonnanceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordonnanceService.createOrdonnance(consultationId, dto));
    }

    // GET — une ordonnance par ID
    @GetMapping("/{ordonnanceId}")
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENTE')")
    public ResponseEntity<OrdonnanceResponseDTO> getOrdonnance(
            @PathVariable Long consultationId,
            @PathVariable Long ordonnanceId) {
        return ResponseEntity.ok(
                ordonnanceService.getOrdonnanceById(consultationId, ordonnanceId));
    }

    // GET — toutes les ordonnances d'une consultation
    @GetMapping
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENTE')")
    public ResponseEntity<List<OrdonnanceResponseDTO>> getOrdonnancesByConsultation(
             @PathVariable Long consultationId) {
        return ResponseEntity.ok(ordonnanceService.getOrdonnancesByConsultation( consultationId));
    }

    // DELETE — supprimer une ordonnance
    @DeleteMapping("/{ordonnanceId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<Void> deleteOrdonnance(
            @PathVariable Long consultationId,
            @PathVariable Long ordonnanceId) {
        ordonnanceService.deleteOrdonnance( consultationId, ordonnanceId);
        return ResponseEntity.noContent().build();
    }
}