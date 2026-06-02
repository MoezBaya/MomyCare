package com.example.MomyCare.controller;

import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.relation.RelationResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.GynecologueMapper;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.Relation;
import com.example.MomyCare.model.StatutRelation;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.RelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relations")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;
    private final SecurityContextService security;
    private final GynecologueMapper gynecologueMapper;

    @GetMapping("/mes-relations")
    @PreAuthorize("hasRole('PATIENTE')")
    public List<RelationResponseDTO> mesRelations() {
        return relationService.getMesRelations();
    }

    @GetMapping("/demandes")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public List<RelationResponseDTO> demandes() {
        return relationService.getDemandesEnAttente();
    }

    @PatchMapping("/{relationId}/terminer")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public RelationResponseDTO terminerRelation(@PathVariable Long relationId) {
        return relationService.terminerRelation(relationId);
    }

    // ─── NEW ENDPOINT for patient to get her own gynaecologist ───────────────
    @GetMapping("/mon-gynecologue")
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<GynecologueResponseDTO> getMyGynecologue() {
        Patiente patiente = security.getPatiente();
        Relation activeRelation = relationService.findActiveRelationByPatiente(patiente.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Aucun gynécologue actif trouvé"));
        return ResponseEntity.ok(gynecologueMapper.toDto(activeRelation.getGynecologue()));
    }
}