package com.example.MomyCare.dto.agenda;

import com.example.MomyCare.dto.creneau.CreneauDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaJournalierDTO {

    private LocalDate date;

    private Long gynecologueId;

    private List<CreneauDTO> creneaux;

    private int totalCreneaux;

    private int creneauxLibres;

    /**
     * Factory method :
     * calcule automatiquement les statistiques de l'agenda.
     */
    public static AgendaJournalierDTO of(
            LocalDate date,
            Long gynecologueId,
            List<CreneauDTO> creneaux
    ) {

        int libres = (int) creneaux.stream()
                .filter(CreneauDTO::isAvailable)
                .count();

        return AgendaJournalierDTO.builder()
                .date(date)
                .gynecologueId(gynecologueId)
                .creneaux(creneaux)
                .totalCreneaux(creneaux.size())
                .creneauxLibres(libres)
                .build();
    }
}