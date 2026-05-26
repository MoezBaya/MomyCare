package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsultation;
    @Column(nullable = false)
    private String compteRendu;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private double tension;
    @Column(nullable = false)
    private int pouls;
    @Column(nullable = false)
    private double saturationOxygene;
    @Column(nullable = false)
    private double temperature;

    private int poulsBebe;

    @ManyToOne
    @JoinColumn(name = "dossier_medicale_id")
    private DossierMedicale dossierMedicale;
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<ResultatAnalyse> analyses = new ArrayList<>();
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Imagerie> imageries = new ArrayList<>();
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ordonnance> ordonnances = new ArrayList<>();

    public void supprimerOrdonnance(Ordonnance ordonnance) {
        ordonnances.remove(ordonnance);
        ordonnance.setConsultation(null);
    }
}
