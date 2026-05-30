package com.example.MomyCare.controller;

import com.example.MomyCare.dto.relation.RelationResponseDTO;
import com.example.MomyCare.service.RelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relations")
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;

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

    @PatchMapping("/{relationId}/terminer") @PreAuthorize("hasRole('GYNECOLOGUE')")
    public RelationResponseDTO terminerRelation(@PathVariable Long relationId ) {
        return relationService.terminerRelation(  relationId );
    }
}