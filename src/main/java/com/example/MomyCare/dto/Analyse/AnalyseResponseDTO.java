package com.example.MomyCare.dto.Analyse;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AnalyseResponseDTO {
    private Long id;
    private String type;
    private String resultat;
    private LocalDate dateAnalyse;
    private String fichierNom;
}
