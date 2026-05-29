package com.example.MomyCare.service;

import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dto.relation.RelationResponseDTO;
import com.example.MomyCare.mapper.RelationMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.Relation;
import com.example.MomyCare.model.StatutRelation;
import com.example.MomyCare.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RelationService {

    private final RelationRepository     relationRepository;
    private final RelationMapper         relationMapper;
    private final SecurityContextService security;

    // ─── Gynéco : terminer une relation ──────────────────────────────────────

    public RelationResponseDTO terminerRelation(Authentication auth, Long relationId) {
        Gynecologue gynecologue = security.getGyneco(auth);

        Relation relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Relation non trouvée"));

        if (!relation.getGynecologue().getId().equals(gynecologue.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Cette relation ne vous appartient pas");
        }

        if (relation.getStatus() != StatutRelation.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Seule une relation active peut être terminée");
        }

        relation.setStatus(StatutRelation.ENDED);
        relation.setDateFin(LocalDate.now());

        return relationMapper.toDto(relationRepository.save(relation));
    }

    // ─── Patiente : voir ses relations ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getMesRelations(Authentication auth) {
        Patiente patiente = security.getPatiente(auth);
        return relationMapper.toDtoList(
                relationRepository.findByPatiente_Id(patiente.getId()));
    }

    // ─── Gynéco : voir les demandes en attente ────────────────────────────────

    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getDemandesEnAttente(Authentication auth) {
        Gynecologue gynecologue = security.getGyneco(auth);
        return relationMapper.toDtoList(
                relationRepository.findByGynecologue_IdAndStatus(
                        gynecologue.getId(), StatutRelation.PENDING));
    }
}