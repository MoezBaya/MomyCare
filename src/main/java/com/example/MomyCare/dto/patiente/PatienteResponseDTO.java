package com.example.MomyCare.dto.patiente;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatienteResponseDTO {
    private Long id;
    private String nom ;
    private String prenom ;
    private String adresse ;
    private String email ;
    private String numeroTelephone ;
    private String ville ;
    private Long matriculeSociale;
    private Long dossierMedicaleId ;
    private List<Long> rendezVousIds ;

}
