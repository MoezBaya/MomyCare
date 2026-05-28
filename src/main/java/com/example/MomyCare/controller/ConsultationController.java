package com.example.MomyCare.controller;

import com.example.MomyCare.dto.consultation.*;
import com.example.MomyCare.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping("/patientes/{patienteId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatiente(
            Authentication auth,
            @PathVariable Long patienteId) {

        return ResponseEntity.ok(
                consultationService.getConsultationsByPatiente(auth, patienteId));
    }

    @PostMapping
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<ConsultationResponseDTO> addConsultation(
            Authentication auth,
            @Valid @RequestBody ConsultationRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consultationService.addConsultation(auth, dto));
    }

    // Patiente → voir ses propres consultations
    @GetMapping("/mes-consultations")
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<List<ConsultationResponseDTO>> getMesConsultations(
            Authentication auth) {
        return ResponseEntity.ok(
                consultationService.getMesConsultations(auth));
    }


}