package com.example.MomyCare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Gynecologue  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long matriculeCachet;

    @Column(nullable = false )
    @Min(value = 0)
    private Integer experience;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro d'agrément est obligatoire")
    private String numeroAgrement;

    @OneToMany(mappedBy = "gynecologue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilite> disponibilites = new ArrayList<>();

    @OneToMany(mappedBy = "gynecologue", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<RendezVous> rendezVousList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "gynecologue" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Demande> demande = new ArrayList<>();

    @OneToMany(mappedBy = "gynecologue" ,cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Patiente> patiente = new ArrayList<>();


}
