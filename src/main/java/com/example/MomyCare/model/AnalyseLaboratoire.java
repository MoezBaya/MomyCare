package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "analyse_laboratoire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyseLaboratoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateAnalyse;

    private String type; // NFS, Glycémie...

    private String resultat;

    private String fichierNom;

    private String fichierType;

    private String fichierPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;
}