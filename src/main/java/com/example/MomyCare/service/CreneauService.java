package com.example.MomyCare.service;

import com.example.MomyCare.dao.DisponibiliteRepository;
import com.example.MomyCare.dao.RendezVousRepository;
import com.example.MomyCare.dto.agenda.AgendaJournalierDTO;
import com.example.MomyCare.dto.creneau.CreneauDTO;
import com.example.MomyCare.model.Disponibilite;
import com.example.MomyCare.model.RendezVous;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreneauService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final RendezVousRepository rendezVousRepository;

    @Value("${momycare.consultation.duree:30}")
    private int dureeConsultationMinutes;

    // ── API publique ─────────────────────────────────────────────────────────


    public List<CreneauDTO> genererCreneaux(Long gynecologueId, LocalDate date) {
        List<Disponibilite> disponibilites =
                disponibiliteRepository.findByGynecologueIdAndJourSemaine(
                        gynecologueId, date.getDayOfWeek()
                );

        // RDV confirmés de ce gynécologue pour la journée (fenêtre 00:00 – 23:59)
        List<LocalDateTime> rdvConfirmes = recupererRdvConfirmes(gynecologueId, date);

        List<CreneauDTO> creneaux = new ArrayList<>();

        for (Disponibilite dispo : disponibilites) {
            creneaux.addAll(decouper(dispo, date, rdvConfirmes));
        }

        // Tri chronologique (plusieurs blocs de dispo possibles dans la journée)
        creneaux.sort(Comparator.comparing(CreneauDTO::getStart));
        return creneaux;
    }

    /**
     * Génère uniquement les créneaux LIBRES pour une date.
     * Utilisé par le front pour afficher les choix de réservation.
     */
    public List<CreneauDTO> genererCreneauxDisponibles(Long gynecologueId, LocalDate date) {
        return genererCreneaux(gynecologueId, date)
                .stream()
                .filter(CreneauDTO::isAvailable)
                .toList();
    }

    /**
     * Retourne l'agenda journalier complet (tous créneaux + statistiques).
     */
    public AgendaJournalierDTO genererAgendaJournalier(Long gynecologueId, LocalDate date) {
        List<CreneauDTO> creneaux = genererCreneaux(gynecologueId, date);
        return AgendaJournalierDTO.of(date, gynecologueId, creneaux);
    }


    public boolean isCreneauLibre(Long gynecologueId, LocalDateTime dateRendezVous) {
        LocalDate date = dateRendezVous.toLocalDate();

        // 1. Le créneau doit tomber dans une disponibilité
        boolean dansUneDisponibilite =
                disponibiliteRepository
                        .findCoveringSlot(gynecologueId, date.getDayOfWeek(), dateRendezVous.toLocalTime())
                        .isPresent();

        if (!dansUneDisponibilite) return false;

        // 2. Aucun RDV confirmé ne doit chevaucher ce créneau
        LocalDateTime fin = dateRendezVous.plusMinutes(dureeConsultationMinutes);
        return !rendezVousRepository.existsConfirmeEntre(gynecologueId, dateRendezVous, fin);
    }

    // ── Méthodes privées ─────────────────────────────────────────────────────

    private List<CreneauDTO> decouper(
            Disponibilite dispo,
            LocalDate date,
            List<LocalDateTime> rdvConfirmes) {

        List<CreneauDTO> result = new ArrayList<>();
        LocalTime cursor = dispo.getHeureDebut();

        while (cursor.plusMinutes(dureeConsultationMinutes).compareTo(dispo.getHeureFin()) <= 0) {
            LocalDateTime start = LocalDateTime.of(date, cursor);
            LocalDateTime end   = start.plusMinutes(dureeConsultationMinutes);
            boolean libre       = !chevauche(start, end, rdvConfirmes);

            result.add(new CreneauDTO(start, end, libre));
            cursor = cursor.plusMinutes(dureeConsultationMinutes);
        }

        return result;
    }

    private boolean chevauche(
            LocalDateTime start,
            LocalDateTime end,
            List<LocalDateTime> rdvConfirmes) {

        return rdvConfirmes.stream().anyMatch(rdvStart -> {
            LocalDateTime rdvEnd = rdvStart.plusMinutes(dureeConsultationMinutes);
            return rdvStart.isBefore(end) && rdvEnd.isAfter(start);
        });
    }


    private List<LocalDateTime> recupererRdvConfirmes(Long gynecologueId, LocalDate date) {
        LocalDateTime debutJournee = date.atStartOfDay();
        LocalDateTime finJournee   = date.atTime(LocalTime.MAX);

        return rendezVousRepository
                .findConfirmesParGynecologueEtPeriode(gynecologueId, debutJournee, finJournee)
                .stream()
                .map(RendezVous::getDateRendezVous)   // LocalDateTime stocké en base
                .toList();
    }
}

