package com.example.MomyCare.dto.DossierMedicale;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DossierMedicaleResponseDTO {

    private Long numeroDossier;

    private String antecedents;
    private String traitement;
    private String maladieChronique;
    private LocalDate dateDeGrosses;

    private Long patienteId;
}
