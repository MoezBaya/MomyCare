package com.example.MomyCare.controller;

import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import com.example.MomyCare.service.DossierMedicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/dossiers")
@RequiredArgsConstructor
public class DossierMedicalController {

    private final DossierMedicalService dossierService;


    @PostMapping("/patiente/{patienteId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<DossierMedicaleResponseDTO> create(
            @PathVariable Long patienteId,
            @RequestBody CreateDossierMedicaleDTO dto) {

        DossierMedicaleResponseDTO response = dossierService.createForPatiente( patienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/patiente/{patienteId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<DossierMedicaleResponseDTO> getByPatienteId(
            Authentication auth,
            @PathVariable Long patienteId) {

        return ResponseEntity.ok(dossierService.getByPatienteId(auth, patienteId));
    }

    @PutMapping("/patiente/{patienteId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<DossierMedicaleResponseDTO> update(

            @PathVariable Long patienteId,
            @RequestBody CreateDossierMedicaleDTO dto) {

        return ResponseEntity.ok(dossierService.update(patienteId, dto));
    }

    @GetMapping("/mon-dossier")
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<DossierMedicaleResponseDTO> getMyDossier(Authentication auth) {

        return ResponseEntity.ok(dossierService.getMyDossier(auth));
    }


}