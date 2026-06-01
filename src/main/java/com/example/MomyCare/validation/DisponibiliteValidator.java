package com.example.MomyCare.validation;

import com.example.MomyCare.dao.DisponibiliteRepository;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.exception.DisponibiliteConflitException;
import com.example.MomyCare.exception.HorairesInvalidesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DisponibiliteValidator {

    private final DisponibiliteRepository disponibiliteRepository;

    // ===================== 1. COHERENCE HEURES =====================

    public void validerCoherenceHeures(DisponibiliteRequestDTO dto) {

        if (dto.getHeureDebut().isAfter(dto.getHeureFin())
                || dto.getHeureDebut().equals(dto.getHeureFin())) {

            throw new HorairesInvalidesException(
                    dto.getHeureDebut().toString(),
                    dto.getHeureFin().toString()
            );
        }
    }

    // ===================== 2. UNICITE =====================

    public void validerUnicite(Long gynecologueId, DisponibiliteRequestDTO dto) {

        boolean doublon =
                disponibiliteRepository.existsByGynecologueIdAndJourSemaineAndHeureDebut(
                        gynecologueId,
                        dto.getJourSemaine(),
                        dto.getHeureDebut()
                );

        if (doublon) {
            throw new DisponibiliteConflitException(
                    "Disponibilité déjà existante pour " +
                            dto.getJourSemaine() + " à " + dto.getHeureDebut()
            );
        }
    }

    // ===================== 3. CHEVAUCHEMENT =====================

    public void validerAbsenceChevauchement(Long gynecologueId, DisponibiliteRequestDTO dto) {

        disponibiliteRepository
                .findByGynecologueIdAndJourSemaine(gynecologueId, dto.getJourSemaine())
                .forEach(existante -> {

                    boolean chevauche =
                            dto.getHeureDebut().isBefore(existante.getHeureFin())
                                    && dto.getHeureFin().isAfter(existante.getHeureDebut());

                    if (chevauche) {
                        throw new DisponibiliteConflitException(
                                "Chevauchement détecté avec [" +
                                        existante.getHeureDebut() + " - " +
                                        existante.getHeureFin() + "] pour " +
                                        dto.getJourSemaine()
                        );
                    }
                });
    }

    // ===================== 4. DATE FUTURE =====================

    public void validerDateFuture(LocalDateTime dateRendezVous) {
        if (dateRendezVous.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La date du rendez-vous doit être dans le futur"
            );
        }
    }
}