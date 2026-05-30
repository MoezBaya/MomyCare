package com.example.MomyCare.controller;


import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.service.DisponibiliteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
@RequiredArgsConstructor
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    @GetMapping("/gyneco/{id}")
    public ResponseEntity<List<DisponibiliteDTO>> getDisponibilitesGyneco(@PathVariable Long id) {
        return ResponseEntity.ok(disponibiliteService.getDisponibilitesGyneco(id));
    }

    // Accessible uniquement au gynécologue pour voir tous ses créneaux
    @GetMapping("/mes-disponibilites")
    public ResponseEntity<List<DisponibiliteDTO>> getMesDisponibilites(Authentication auth) {
        return ResponseEntity.ok(disponibiliteService.getMesDisponibilites(auth));
    }

    @PostMapping
    public ResponseEntity<List<DisponibiliteDTO>> ajouterDisponibilites(
            Authentication auth,
            @Valid @RequestBody List<DisponibiliteRequestDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(disponibiliteService.ajouterDisponibilites(auth, dtos));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerDisponibilite(
            Authentication auth,
            @PathVariable Long id) {
        disponibiliteService.supprimerDisponibilite(auth, id);
        return ResponseEntity.noContent().build();
    }
}

