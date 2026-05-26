package com.example.MomyCare.dao;

import com.example.MomyCare.model.Relation;
import com.example.MomyCare.model.StatutRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RelationRepository extends JpaRepository<Relation, Long> {

    Optional<Relation> findByPatiente_IdAndGynecologue_IdAndStatus(
            Long patienteId, Long gynecologueId, StatutRelation statut);

    List<Relation> findByPatiente_Id(Long patienteId);

    List<Relation> findByGynecologue_IdAndStatus(Long gynecologueId, StatutRelation statut);

    boolean existsByPatiente_IdAndGynecologue_IdAndStatusIn(
            Long patienteId, Long gynecologueId, List<StatutRelation> statuts);
}