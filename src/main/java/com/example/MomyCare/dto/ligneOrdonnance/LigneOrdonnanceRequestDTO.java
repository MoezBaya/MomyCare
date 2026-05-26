package com.example.MomyCare.dto.ligneOrdonnance;

import com.example.MomyCare.model.Medicament;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneOrdonnanceRequestDTO {

    @NotBlank
    private String dose;

    @NotBlank
    private String frequence;

    @NotNull
    @Min(1)
    @Max(999)
    private Integer quantite;

    private String instructions;

    @Min(1)
    private Integer dureeTraitementJours;

    @NotNull
    private Long medicamentId;
}
