package com.example.MomyCare.dao;

import com.example.MomyCare.model.Demande;
import com.example.MomyCare.model.DemandeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    boolean existsByPatienteIdAndGynecologueId(Long patienteId, Long gynecoId);

    List<Demande> findByGynecologueIdAndStatus(Long gynecoId, DemandeStatus status);

    Optional<Demande> findByIdAndGynecologueId(Long demandeId, Long id);
}
