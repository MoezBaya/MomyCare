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
    public RelationResponseDTO terminerRelation( Authentication auth, Long relationId ) {
        Gynecologue gynecologue = getAuthenticatedGyneco(auth);
        Relation relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Relation non trouvée" ));
        validateRelationOwnership(relation, gynecologue);
        if (relation.getStatus() != StatutRelation.ACTIVE) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Seule une relation active peut être terminée" );
        }
        relation.setStatus(StatutRelation.ENDED);
        relation.setDateFin(LocalDate.now());
        Relation savedRelation = relationRepository.save(relation);
        return relationMapper.toDto(savedRelation); }

    // ─── PATIENTE: Voir ses relations ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getMesRelations(Authentication auth) {
        Patiente patiente = getAuthenticatedPatiente(auth);
        List<Relation> relations = relationRepository.findByPatiente_Id(patiente.getId());
        return relationMapper.toDtoList(relations); }

    // ─── GYNÉCO: Voir les demandes en attente ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getDemandesEnAttente(Authentication auth ) {
        Gynecologue gynecologue = getAuthenticatedGyneco(auth);
        List<Relation> relations = relationRepository
                .findByGynecologue_IdAndStatus( gynecologue.getId(), StatutRelation.PENDING );
        return relationMapper.toDtoList(relations); }

    // ─── Helpers privés ───────────────────────────────────────────────────────
    private Patiente getAuthenticatedPatiente(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return patienteRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));
    }

    private Gynecologue getAuthenticatedGyneco(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return gynecologueRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));
    }
    private void validateRelationOwnership( Relation relation, Gynecologue gynecologue ) {
        if (!relation.getGynecologue().getId() .equals(gynecologue.getId())) {
            throw new ResponseStatusException( HttpStatus.FORBIDDEN, "Cette relation ne vous appartient pas" );
        }
    }
}