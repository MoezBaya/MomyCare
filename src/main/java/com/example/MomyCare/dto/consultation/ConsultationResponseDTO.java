package com.example.MomyCare.dto.consultation;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationResponseDTO {

    private Long id;
    private String compteRendu;
    private Double tension;
    private Integer pouls;
    private Double saturationOxygene;
    private Double temperature;
    private Integer poulsBebe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long dossierMedicaleId;
}