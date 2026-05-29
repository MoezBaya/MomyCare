package com.example.MomyCare.dto.Imagerie;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ImagerieResponseDTO {
    private Long id;
    private String type;
    private String description;
    private LocalDate dateImagerie;
    private String fichierNom;
}
