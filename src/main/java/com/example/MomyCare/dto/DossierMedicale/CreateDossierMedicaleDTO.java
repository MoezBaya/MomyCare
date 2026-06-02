package com.example.MomyCare.dto.DossierMedicale;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDossierMedicaleDTO {

    private String antecedents;
    private String traitement;
    private String maladieChronique;
    private LocalDate dateDeGrosses;
    private String groupeSangin ;
}
