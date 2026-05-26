package com.example.MomyCare.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DossierMedicale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroDossier;

    private String antecedents;

    private String traitement;

    private String maladieChronique;

    private LocalDate dateDeGrosses ;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patiente_id", nullable = false, unique = true)
    private Patiente patiente;

    @OneToMany(mappedBy = "dossierMedicale", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Consultation> consultations = new ArrayList<>();


}