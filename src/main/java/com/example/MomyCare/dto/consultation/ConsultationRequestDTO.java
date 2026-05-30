package com.example.MomyCare.dto.consultation;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRequestDTO {

    @NotBlank(message = "Le compte rendu est obligatoire")
    private String compteRendu;

    @NotNull @DecimalMin("0.0")
    private Double tension;
    @NotNull @Min(0) @Max(300)
    private Integer pouls;
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double saturationOxygene;
    @NotNull @DecimalMin("30.0") @DecimalMax("45.0")
    private Double temperature;
    @Min(0) @Max(300)
    private Integer poulsBebe;
    @NotNull(message = "L'ID de la patiente est obligatoire")
    private Long patienteId;
}