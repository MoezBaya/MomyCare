package com.example.MomyCare.controller;

import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.dto.creneau.CreneauDTO;
import com.example.MomyCare.dto.agenda.AgendaJournalierDTO;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.CreneauService;
import com.example.MomyCare.service.DisponibiliteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
@RequiredArgsConstructor
@Tag(name = "disponibilité" , description = "Gestion des disponibiltés de Gynecologue")
@SecurityRequirement(name = "bearerAuth")
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;
    private final CreneauService creneauService;
    private final SecurityContextService securityContextService;

    // ================== GET disponibilités d’un gynéco ==================

    @Operation(summary = "Récuperer les disponibilités de Gynecologue ",
            description = "Permet d'une patiente de consulter la disponibilité de Gynecologue")
    @GetMapping("/gyneco/{gynecologueId}")
    public ResponseEntity<List<DisponibiliteDTO>> getDisponibilitesGyneco(
            @PathVariable Long gynecologueId) {

        return ResponseEntity.ok(
                disponibiliteService.getDisponibilitesParGyneco(gynecologueId)
        );
    }

    // ================== MES disponibilités ==================

    @GetMapping("/mes-disponibilites")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<List<DisponibiliteDTO>> getMesDisponibilites() {

        Long gynecoId = securityContextService.getGyneco().getId();

        return ResponseEntity.ok(
                disponibiliteService.getMesDisponibilites(gynecoId)
        );
    }

    // ================== CREATE disponibilité ==================

    @PostMapping
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<DisponibiliteDTO> creerDisponibilite(
            @Valid @RequestBody DisponibiliteRequestDTO dto) {

        Long gynecoId = securityContextService.getGyneco().getId();

        DisponibiliteDTO created =
                disponibiliteService.creerDisponibilite(gynecoId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ================== DELETE ==================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<Void> supprimerDisponibilite(
            @PathVariable Long id) {

        Long gynecoId = securityContextService.getGyneco().getId();

        disponibiliteService.deleteDisponibilite(id, gynecoId);

        return ResponseEntity.noContent().build();
    }

    // ================== UPDATE ==================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<DisponibiliteDTO> mettreAJour(
            @PathVariable Long id,
            @Valid @RequestBody DisponibiliteRequestDTO dto) {

        Long gynecoId = securityContextService.getGyneco().getId();

        return ResponseEntity.ok(
                disponibiliteService.mettreAJourDisponibilite(id, gynecoId, dto)
        );
    }

    // ================== CRENEAUX ==================

    @GetMapping("/gyneco/{gynecologueId}/creneaux")
    public ResponseEntity<List<CreneauDTO>> getCreneaux(
            @PathVariable Long gynecologueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean disponiblesUniquement) {

        List<CreneauDTO> creneaux = disponiblesUniquement
                ? creneauService.genererCreneauxDisponibles(gynecologueId, date)
                : creneauService.genererCreneaux(gynecologueId, date);

        return ResponseEntity.ok(creneaux);
    }

    // ================== AGENDA ==================

    @GetMapping("/gyneco/{gynecologueId}/agenda")
    @PreAuthorize("hasAnyRole('GYNECOLOGUE','PATIENTE')")
    public ResponseEntity<AgendaJournalierDTO> getAgenda(
            @PathVariable Long gynecologueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(
                creneauService.genererAgendaJournalier(gynecologueId, date)
        );
    }
}