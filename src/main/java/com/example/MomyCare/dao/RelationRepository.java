package com.example.MomyCare.dao;

import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.Relation;
import com.example.MomyCare.model.StatutRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationRepository extends JpaRepository<Relation, Long> {
    List<Relation> findByPatiente_Id(Long patienteId);
    List<Relation> findByGynecologue_IdAndStatus(Long gynecologueId, StatutRelation statut);
    boolean existsByPatiente_IdAndGynecologue_IdAndStatus(Long id, Long id1, StatutRelation statutRelation);
}