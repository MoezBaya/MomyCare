package com.example.MomyCare.dao;

import com.example.MomyCare.model.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {

    /**
     * Toutes les disponibilités d'un gynécologue (tous jours confondus).
     * Méthode existante — NOM INCHANGÉ.
     */
    List<Disponibilite> findByGynecologueId(Long gynecologueId);

    /**
     * Disponibilités d'un gynécologue pour un jour de semaine précis.
     * Utilisée par CreneauService pour filtrer avant de générer les créneaux.
     */
    List<Disponibilite> findByGynecologueIdAndJourSemaine(Long gynecologueId, DayOfWeek jourSemaine);

    /**
     * Vérifie l'existence d'un doublon avant création.
     * Permet d'afficher un message métier clair au lieu d'une ConstraintViolationException.
     */
    @Query("""
        SELECT COUNT(d) > 0 FROM Disponibilite d
        WHERE d.gynecologue.id = :gynecologueId
          AND d.jourSemaine    = :jourSemaine
          AND d.heureDebut     = :heureDebut
    """)
    boolean existsByGynecologueIdAndJourSemaineAndHeureDebut(
            @Param("gynecologueId") Long gynecologueId,
            @Param("jourSemaine")   DayOfWeek jourSemaine,
            @Param("heureDebut")    java.time.LocalTime heureDebut
    );

    /**
     * Recherche une disponibilité spécifique (pour la validation de chevauchement).
     */
    @Query("""
        SELECT d FROM Disponibilite d
        WHERE d.gynecologue.id = :gynecologueId
          AND d.jourSemaine    = :jourSemaine
          AND d.heureDebut    <= :heure
          AND d.heureFin      >  :heure
    """)
    Optional<Disponibilite> findCoveringSlot(
            @Param("gynecologueId") Long gynecologueId,
            @Param("jourSemaine") DayOfWeek jourSemaine,
            @Param("heure")         java.time.LocalTime heure
    );
}


