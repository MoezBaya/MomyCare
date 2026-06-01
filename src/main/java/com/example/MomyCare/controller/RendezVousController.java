package com.example.MomyCare.controller;

import com.example.MomyCare.dto.rdv.RendezVousRequestDTO;
import com.example.MomyCare.dto.rdv.RendezVousResponseDTO;
import com.example.MomyCare.service.RendezVousService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rdv")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rdvService;

    // Patiente → demander un RDV
    @PostMapping
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<RendezVousResponseDTO> demanderRdv(
            @Valid @RequestBody RendezVousRequestDTO dto) {
        RendezVousResponseDTO response = rdvService.demanderRdv(dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{rdvId}/repondre")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<RendezVousResponseDTO> repondreRdv(
            Authentication auth,
            @PathVariable Long rdvId,
            @RequestParam boolean accepter) {
        return ResponseEntity.ok(rdvService.repondreRdv( rdvId, accepter));
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<List<RendezVousResponseDTO>> getRdvEnAttente(
            Authentication auth) {
        return ResponseEntity.ok(rdvService.getMesRdvEnAttente());
    }

    @GetMapping("/mes-rdv")
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<List<RendezVousResponseDTO>> getMesRdv() {
        return ResponseEntity.ok(rdvService.getMesRdv());
    }

    @GetMapping("/gyneco/mes-rdv")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<List<RendezVousResponseDTO>> getGynecoRdv() {
        return ResponseEntity.ok(rdvService.getGynecoRdv());
    }
}
