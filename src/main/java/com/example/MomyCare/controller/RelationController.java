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
    public List<RelationResponseDTO> mesRelations(Authentication auth) {
        return relationService.getMesRelations(auth);
    }

    @GetMapping("/demandes")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public List<RelationResponseDTO> demandes(Authentication auth) {
        return relationService.getDemandesEnAttente(auth);
    }

    @PatchMapping("/{relationId}/terminer") @PreAuthorize("hasRole('GYNECOLOGUE')")
    public RelationResponseDTO terminerRelation( Authentication auth,
                                                 @PathVariable Long relationId ) {
        return relationService.terminerRelation( auth, relationId );
    }
}