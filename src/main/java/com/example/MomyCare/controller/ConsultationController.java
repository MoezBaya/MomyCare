package com.example.MomyCare.controller;

import com.example.MomyCare.dto.consultation.ConsultationRequestDTO;
import com.example.MomyCare.dto.consultation.ConsultationResponseDTO;
import com.example.MomyCare.service.ConsultationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor

@Tag(
        name = "Consultations",
        description = "Gestion des consultations médicales"
)

@SecurityRequirement(name = "bearerAuth")
public class ConsultationController {

    private final ConsultationService consultationService;

    @Operation(
            summary = "Récupérer les consultations d’une patiente",
            description = "Permet au gynécologue de consulter l’historique des consultations d’une patiente"
    )
    @GetMapping("/patientes/{patienteId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<List<ConsultationResponseDTO>> getConsultationsByPatiente(
            Authentication authentication,
            @PathVariable Long patienteId
    ) {

        return ResponseEntity.ok(
                consultationService.getConsultationsByPatiente(authentication, patienteId));
    }

    @Operation(
            summary = "Ajouter une consultation",
            description = "Permet au gynécologue d’ajouter une nouvelle consultation médicale"
    )
    @PostMapping
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<ConsultationResponseDTO> addConsultation(
            Authentication authentication,
            @Valid @RequestBody ConsultationRequestDTO dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consultationService.addConsultation(authentication, dto));
    }

    @Operation(
            summary = "Récupérer mes consultations",
            description = "Permet à la patiente de consulter son historique médical"
    )
    @GetMapping("/mes-consultations")
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<List<ConsultationResponseDTO>> getMesConsultations(
            Authentication authentication
    ) {
        return ResponseEntity.ok(consultationService.getMesConsultations(authentication));
    }
}