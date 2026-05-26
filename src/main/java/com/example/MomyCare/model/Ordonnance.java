package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Ordonnance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrdonnance ;

    @Column(nullable = false)
    private String numOrdonnance ;

    @Column(nullable = false)
    private String cachets ;

    @Column(nullable = false)
    private String signature;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "ordonnance", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<LigneOrdonnance> ligneOrdonnances = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    // methode utilitaire
    public void ajouterLigneOrdonance(LigneOrdonnance ligne) {
        ligneOrdonnances.add(ligne);
        ligne.setOrdonnance(this);
    }

    public void supprimerLigneOrdonance(LigneOrdonnance ligne) {
        ligneOrdonnances.remove(ligne);
        ligne.setOrdonnance(null);
    }

}
