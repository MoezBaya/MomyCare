package com.example.MomyCare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patiente{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long matriculeSociale;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "patiente", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<RendezVous> rendezVousList =  new ArrayList<>();

    @OneToOne(mappedBy = "patiente",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private DossierMedicale dossierMedicale;;

    @OneToMany(mappedBy = "patiente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relation> relations = new ArrayList<>();

}
