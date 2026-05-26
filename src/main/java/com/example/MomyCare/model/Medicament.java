package com.example.MomyCare.model;

import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medicament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeMedicament;

    @Column(nullable = false)
    private String nomMedicament;

    @OneToMany(mappedBy = "medicament" ,  cascade = CascadeType.ALL ,  orphanRemoval = true)
    private List<LigneOrdonnance> ligneOrdonnances = new ArrayList<>();

    // Methode utilistaire
    public void ajouterLigneOrdonnance(LigneOrdonnance ligne ) {
        this.ligneOrdonnances.add(ligne);
        ligne.setMedicament(this);
    }

    public void supprimerLigneOrdonnance(LigneOrdonnance ligne ) {
        this.ligneOrdonnances.remove(ligne);
        ligne.setMedicament(null);
    }
}
