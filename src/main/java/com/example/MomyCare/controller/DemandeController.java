package com.example.MomyCare.controller;

import com.example.MomyCare.dto.demande.DemandeRequestDTO;
import com.example.MomyCare.dto.demande.DemandeResponseDTO;
import com.example.MomyCare.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;


    @PostMapping
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<String> envoyer(
            Authentication auth,
            @Valid @RequestBody DemandeRequestDTO dto) {

        demandeService.envoyerDemande(auth, dto);
        return ResponseEntity.ok("Demande envoyée");
    }

    // ACCEPT
    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<String> accept(
            Authentication auth,
            @PathVariable Long id) {

        demandeService.accepterDemande(auth, id);
        return ResponseEntity.ok("Demande acceptée");
    }


    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<String> reject(
            Authentication auth,
            @PathVariable Long id) {

        demandeService.refuserDemande(auth, id);
        return ResponseEntity.ok("Demande refusée");
    }


    @GetMapping
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<List<DemandeResponseDTO>> getDemandes(
            Authentication auth) {

        return ResponseEntity.ok(
                demandeService.getDemandesGyneco(auth)
        );
    }
}