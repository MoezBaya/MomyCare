package com.example.MomyCare.service;

import com.example.MomyCare.dto.relation.RelationResponseDTO;
import com.example.MomyCare.model.Relation;

import java.util.List;
import java.util.Optional;

public interface RelationService {
    List<RelationResponseDTO> getMesRelations();

    List<RelationResponseDTO> getDemandesEnAttente();

    RelationResponseDTO terminerRelation(Long relationId);

    Optional<Relation> findActiveRelationByPatiente(Long id);
}
