package com.example.MomyCare.validation;

import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.exception.DisponibiliteOverlapException;
import com.example.MomyCare.exception.DuplicateDisponibiliteException;
import com.example.MomyCare.exception.InvalidTimeException;
import com.example.MomyCare.model.Disponibilite;
import com.example.MomyCare.dao.DisponibiliteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DisponibiliteValidator {

    private final DisponibiliteRepository disponibiliteRepository;

    // ---------- Validation de date ----------
    public void validerDateFuture(LocalDateTime dateRdv) {
        if (dateRdv == null) throw new InvalidTimeException("La date est obligatoire.");
        if (dateRdv.isBefore(LocalDateTime.now()))
            throw new InvalidTimeException("La date ne peut pas être dans le passé.");
    }

    // ---------- Cohérence des heures ----------
    public void validerCoherenceHeures(DisponibiliteRequestDTO dto) {
        if (dto.getHeureDebut() == null || dto.getHeureFin() == null)
            throw new InvalidTimeException("Les heures sont obligatoires.");
        if (!dto.getHeureFin().isAfter(dto.getHeureDebut()))
            throw new InvalidTimeException("L'heure de fin doit être après l'heure de début.");
    }

    // ---------- Unicité ----------
    public void validerUnicite(Long gynecologueId, DisponibiliteRequestDTO dto) {
        DayOfWeek jour = dto.getDate().getDayOfWeek();
        if (disponibiliteRepository.existsByGynecologueIdAndJourSemaineAndHeureDebut(gynecologueId, jour, dto.getHeureDebut()))
            throw new DuplicateDisponibiliteException("Ce créneau existe déjà pour ce jour.");
    }

    // ---------- Chevauchement ----------
    public void validerAbsenceChevauchement(Long gynecologueId, DisponibiliteRequestDTO dto) {
        DayOfWeek jour = dto.getDate().getDayOfWeek();
        List<Disponibilite> existantes = disponibiliteRepository.findByGynecologueIdAndJourSemaine(gynecologueId, jour);
        for (Disponibilite dispo : existantes) {
            if (dto.getHeureDebut().isBefore(dispo.getHeureFin()) && dto.getHeureFin().isAfter(dispo.getHeureDebut())) {
                throw new DisponibiliteOverlapException("Ce créneau chevauche une disponibilité existante.");
            }
        }
    }

    // ---------- Pour mise à jour (exclure l'entité modifiée) ----------
    public void validerUnicitePourUpdate(Long gynecologueId, Long idExclu, DisponibiliteRequestDTO dto) {
        DayOfWeek jour = dto.getDate().getDayOfWeek();
        if (disponibiliteRepository.existsByGynecologueIdAndJourSemaineAndHeureDebutAndIdNot(gynecologueId, jour, dto.getHeureDebut(), idExclu))
            throw new DuplicateDisponibiliteException("Une autre disponibilité existe déjà pour ce jour.");
    }

    public void validerAbsenceChevauchementPourUpdate(Long gynecologueId, Long idExclu, DisponibiliteRequestDTO dto) {
        DayOfWeek jour = dto.getDate().getDayOfWeek();
        List<Disponibilite> existantes = disponibiliteRepository.findByGynecologueIdAndJourSemaine(gynecologueId, jour);
        for (Disponibilite dispo : existantes) {
            if (dispo.getId().equals(idExclu)) continue;
            if (dto.getHeureDebut().isBefore(dispo.getHeureFin()) && dto.getHeureFin().isAfter(dispo.getHeureDebut())) {
                throw new DisponibiliteOverlapException("Ce créneau chevauche une autre disponibilité.");
            }
        }
    }
}