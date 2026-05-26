package com.example.MomyCare.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultatAnalyse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAnalyse;
    @CreationTimestamp
    @Column(name = "date_analyse")
    private LocalDateTime dateAnalyse;

    @Column(nullable = false ,name = "resultat_biologique")
    private String resultatBiologique;

    @Column(nullable = false , name = "cachet_Laboratoire")
    private String cachetLaboratoire;


    @Column(name = "type_analyse", nullable = false, length = 50)
    @NotNull(message = "Le type d'analyse est obligatoire")
    private String typeAnalyse;

    @Column(name = "valeurs_reference", length = 500)
    private String valeursReference;

    @Column(name = "interpretation", length = 1000)
    private String interpretation;


    @Column(name = "biologiste_nom")
    private String biologisteNom;


    @Column(name = "nom_laboratoire")
    private String nomLaboratoire;

    @Column(name = "numero_prelevement", length = 50)
    private String numeroPrelevement;

    @Column(name = "date_prelevement")
    private LocalDateTime datePrelevement;

    @Column(name = "date_reception")
    private LocalDateTime dateReception;

    @Column(name = "fichier_pdf", length = 500)
    private String fichierPdf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;


}
