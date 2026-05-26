package com.example.MomyCare.dto.ligneOrdonnance;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneOrdonnanceResponseDTO {

    private Long idLigneOrdonnance;

    private String dose;
    private String frequence;
    private Integer quantite;
    private String instructions;
    private Integer dureeTraitementJours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long medicamentId;
    private String nomMedicament;

}