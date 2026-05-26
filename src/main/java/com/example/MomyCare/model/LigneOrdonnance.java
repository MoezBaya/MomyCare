package com.example.MomyCare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LigneOrdonnance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLigneOrdonnance;

    @Column(nullable = false)
    private String dose;

    @Column(nullable = false)
    private String frequence;

    @Column(nullable = false)
    @Min(1)
    @Max(999)
    private Integer quantite;

    @Column(length = 500)
    private String instructions;

    @Column(name = "duree_traitement_jours")
    @Min(1)
    private Integer dureeTraitementJours;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordonnance_id") // ✔️ corrigé
    private Ordonnance ordonnance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicament_id")
    private Medicament medicament;

    public String getDescription() {
        return String.format("%s - %s: %s, %s, Quantité: %d",
                medicament != null ? medicament.getNomMedicament() : "N/A",
                dose,
                frequence,
                instructions != null ? instructions : "",
                quantite);
    }
}
