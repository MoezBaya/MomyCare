package com.example.MomyCare.dto.gynecologue;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GynecologueResponseDTO {

    private Long id;
    private String nom ;
    private String prenom ;
    private String adresse ;
    private String email ;
    private String numeroTelephone ;
    private String ville ;
    private LocalDate dateDeNaissance;
    private Long matriculeCachet;
    private String numeroAgrement;
    private Integer experience;
    private List<Long> disponibiliteIds;
    private List<Long> rendezVousIds;
}
