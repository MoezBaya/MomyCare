package com.example.MomyCare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "disponibilites",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_dispo_gyneco_jour_debut",
                columnNames = {"gynecologue_id", "jour_semaine", "heure_debut"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Jour de la semaine (lundi=MONDAY … dimanche=SUNDAY). */
    @NotNull(message = "Le jour de la semaine est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", nullable = false, length = 15)
    private DayOfWeek jourSemaine;

    /** Heure de début du bloc de travail (ex : 08:00). */
    @NotNull(message = "L'heure de début est obligatoire")
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    /** Heure de fin du bloc de travail (ex : 17:00). */
    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    /** Gynécologue propriétaire de cette disponibilité. */
    @NotNull(message = "Le gynécologue est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gynecologue_id", nullable = false)
    private Gynecologue gynecologue;

    // ── Invariants métier ────────────────────────────────────────────────────

    @PrePersist
    @PreUpdate
    void validateHeures() {
        if (heureDebut != null && heureFin != null && !heureFin.isAfter(heureDebut)) {
            throw new IllegalStateException(
                    "L'heure de fin (%s) doit être strictement après l'heure de début (%s)"
                            .formatted(heureFin, heureDebut)
            );
        }
    }
}
