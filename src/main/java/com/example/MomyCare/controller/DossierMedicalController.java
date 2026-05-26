package com.example.MomyCare.controller;

import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import com.example.MomyCare.service.DossierMedicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DossierMedicalController {

    private final DossierMedicalService service;

    @PostMapping("/patiente/{patienteId}/dossiers")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public DossierMedicaleResponseDTO create(@RequestBody CreateDossierMedicaleDTO dto,
                                             @PathVariable Long patienteId,
                                             Authentication auth) {
        System.out.println("Authentication: " + auth);
        if (auth != null) {
            System.out.println("Principal: " + auth.getPrincipal());
            System.out.println("Authorities: " + auth.getAuthorities());
        }
        return service.createForPatiente(patienteId , dto);
    }


    @PutMapping("/patiente/{patienteId}/dossiers")
    @PreAuthorize("hasRole('GYNECOLOGUE') ")
    public DossierMedicaleResponseDTO update(@PathVariable Long patienteId,
                                     @RequestBody CreateDossierMedicaleDTO dto) {
        return service.update(patienteId, dto);
    }

    //
    @GetMapping("/dossiers")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public List<DossierMedicaleResponseDTO> getAll() {
        return service.getAll();
    }

    //
    @GetMapping("/patiente/{patienteId}/dossiers")
    @PreAuthorize("hasRole('GYNECOLOGUE') ")
    public DossierMedicaleResponseDTO getByIPatientd(@PathVariable Long patienteId) {
        return service.getByPatientId(patienteId);
    }

    // PATIENTE
    @GetMapping("/dossiers/me")
    @PreAuthorize("hasRole('PATIENTE')")
    public DossierMedicaleResponseDTO getMyDossier(Authentication auth) {
        return service.getMyDossier(auth);
    }
}