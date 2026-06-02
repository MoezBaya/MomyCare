package com.example.MomyCare.service;

import com.example.MomyCare.dto.agenda.AgendaJournalierDTO;
import com.example.MomyCare.dto.creneau.CreneauDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CreneauService {
    List<CreneauDTO> genererCreneauxDisponibles(Long gynecologueId, LocalDate date);

    List<CreneauDTO> genererCreneaux(Long gynecologueId, LocalDate date);

    AgendaJournalierDTO genererAgendaJournalier(Long gynecologueId, LocalDate date);

    boolean isCreneauLibre(@NotNull(message = "L'ID du gynécologue est obligatoire") Long gynecologueId, @NotNull(message = "La date est obligatoire") @Future(message = "La date doit être dans le futur") LocalDateTime dateRendezVous);
}
