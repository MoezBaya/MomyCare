package com.example.MomyCare.service;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dto.relation.RelationResponseDTO;
import com.example.MomyCare.mapper.RelationMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.UserDetailsImpl;
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

    private final RelationRepository relationRepository;
    private final PatienteRepository patienteRepository;
    private final GynecologueRepository gynecologueRepository;
    private final RelationMapper relationMapper;

    // ─── GYNÉCO: Terminer une relation (fin grossesse) ────────────────────────
    @Transactional
    public RelationResponseDTO terminerRelation(
            Authentication auth, Long relationId, String notes) {

        Gynecologue gynecologue = getGyneco(auth);

        Relation relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Relation non trouvée"));

        if (!relation.getGynecologue().getId().equals(gynecologue.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Cette relation ne vous appartient pas");
        }

        if (!relation.getStatus().equals(StatutRelation.ACTIVE)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Seule une relation active peut être terminée");
        }

        relation.setStatus(StatutRelation.ENDED);
        relation.setDateFin(LocalDate.now());

        return relationMapper.toDto(relationRepository.save(relation));
    }

    // ─── PATIENTE: Voir ses relations ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getMesRelations(Authentication auth) {
        Patiente patiente = getPatiente(auth);
        return relationMapper.toDtoList(
                relationRepository.findByPatiente_Id(patiente.getId())
        );
    }

    // ─── GYNÉCO: Voir les demandes en attente ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getDemandesEnAttente(Authentication auth) {
        Gynecologue gynecologue = getGyneco(auth);
        return relationMapper.toDtoList(
                relationRepository.findByGynecologue_IdAndStatus(
                        gynecologue.getId(), StatutRelation.PENDING)
        );
    }

    // ─── Helpers privés ───────────────────────────────────────────────────────
    private Patiente getPatiente(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return patienteRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));
    }

    private Gynecologue getGyneco(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return gynecologueRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));
    }
}