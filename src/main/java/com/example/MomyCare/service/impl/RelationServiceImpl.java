package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dto.relation.RelationResponseDTO;
import com.example.MomyCare.exception.BadRequestException;
import com.example.MomyCare.exception.ForbiddenException;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.RelationMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.Relation;
import com.example.MomyCare.model.StatutRelation;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.RelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RelationServiceImpl implements RelationService {

    private final RelationRepository     relationRepository;
    private final RelationMapper         relationMapper;
    private final SecurityContextService security;

    public RelationResponseDTO terminerRelation(Long relationId) {
        Gynecologue gynecologue = security.getGyneco();

        Relation relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new ResourceNotFoundException("Relation non trouvée"));

        if (!relation.getGynecologue().getId().equals(gynecologue.getId())) {
            throw new ForbiddenException("Cette relation ne vous appartient pas");
        }

        if (relation.getStatus() != StatutRelation.ACTIVE) {
            throw new BadRequestException("Seule une relation active peut être terminée");
        }

        relation.setStatus(StatutRelation.ENDED);
        relation.setDateFin(LocalDate.now());

        return relationMapper.toDto(relationRepository.save(relation));
    }

    public Optional<Relation> findActiveRelationByPatiente(Long patienteId) {
        return relationRepository.findByPatiente_IdAndStatus(patienteId, StatutRelation.ACTIVE);
    }


    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getMesRelations() {
        Patiente patiente = security.getPatiente();
        return relationMapper.toDtoList(relationRepository.findByPatiente_Id(patiente.getId()));
    }

    @Transactional(readOnly = true)
    public List<RelationResponseDTO> getDemandesEnAttente() {
        Gynecologue gynecologue = security.getGyneco();
        return relationMapper.toDtoList(
                relationRepository.findByGynecologue_IdAndStatus(
                        gynecologue.getId(), StatutRelation.PENDING));
    }
}