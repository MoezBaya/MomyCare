package com.example.MomyCare.dao;

import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.RendezVous;
import com.example.MomyCare.model.StatusRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    // ── existants corrigés ─────────────────────────────

    List<RendezVous> findByPatiente_Id(Long patienteId);

    List<RendezVous> findByGynecologue_Id(Long gynecologueId);

    List<RendezVous> findByGynecologue_IdAndStatusRDV(Long gynecologueId, StatusRDV status);

    // ── QUERY corrigée ────────────────────────────────

    @Query("""
        SELECT r FROM RendezVous r
        WHERE r.gynecologue.id = :gynecologueId
          AND r.statusRDV = com.example.MomyCare.model.StatusRDV.CONFIRME
          AND r.dateRendezVous >= :debut
          AND r.dateRendezVous < :fin
        ORDER BY r.dateRendezVous
    """)
    List<RendezVous> findConfirmesParGynecologueEtPeriode(
            @Param("gynecologueId") Long gynecologueId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
        SELECT COUNT(r) > 0 FROM RendezVous r
        WHERE r.gynecologue.id = :gynecologueId
          AND r.statusRDV = com.example.MomyCare.model.StatusRDV.CONFIRME
          AND r.dateRendezVous >= :debut
          AND r.dateRendezVous < :fin
    """)
    boolean existsConfirmeEntre(
            @Param("gynecologueId") Long gynecologueId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );
}