package com.example.MomyCare.controller;

import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import com.example.MomyCare.service.LigneOrdonnanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ordonnances/{ordonnanceId}/lignes")
public class LigneOrdonnanceController {

    private final LigneOrdonnanceService ligneOrdonnanceService;

    @PostMapping()
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<LigneOrdonnanceResponseDTO> createLigneOrdonnance(@PathVariable Long ordonnanceId
                                                        , @RequestBody LigneOrdonnanceRequestDTO ligneOrdonnance,
                                                                       Authentication auth) {
        LigneOrdonnanceResponseDTO responseDTO = ligneOrdonnanceService.addLigne(ordonnanceId, ligneOrdonnance , auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PreAuthorize("hasRole('GYNECOLOGUE')")
    @PutMapping("/{ligneOrodnnanceId}")
    public ResponseEntity<LigneOrdonnanceResponseDTO> updateLigneOrdonnance( @PathVariable Long ordonnanceId,
                                                                             @PathVariable Long ligneOrodnnanceId ,
                                                                             @RequestBody LigneOrdonnanceRequestDTO dto,
                                                                             Authentication auth) {
        LigneOrdonnanceResponseDTO responseDto = ligneOrdonnanceService.updateLigne(ordonnanceId, ligneOrodnnanceId , dto ,auth);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{ligneOrdonnanceId}")
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENT')")
    public ResponseEntity<LigneOrdonnanceResponseDTO> getOrdonnance(
            @PathVariable Long ordonnanceId,
            @PathVariable Long ligneOrdonnanceId) {

        return ResponseEntity.ok(ligneOrdonnanceService.getLigneById(ordonnanceId,ligneOrdonnanceId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENT')")
    public ResponseEntity<List<LigneOrdonnanceResponseDTO>> getOrdonnancesByDossier(
            @PathVariable Long ordonnanceId) {

        return ResponseEntity.ok(ligneOrdonnanceService.getLignesByOrdonnance(ordonnanceId));
    }

    @DeleteMapping("/{ligneOrdonnanceId}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<Void> deleteLigneOrdonnance(
            @PathVariable Long ligneOrdonnanceId,
            @PathVariable Long ordonnanceId,
            Authentication auth) {

        ligneOrdonnanceService.deleteLigne(ligneOrdonnanceId , ordonnanceId,auth);
        return ResponseEntity.noContent().build();
    }



}
