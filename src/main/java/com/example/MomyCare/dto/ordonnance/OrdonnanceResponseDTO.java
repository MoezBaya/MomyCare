package com.example.MomyCare.dto.ordonnance;

import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdonnanceResponseDTO {

    private Long idOrdonance;

    private String numOrdonance;

    private String cachets;

    private String signature;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long consultationId;

    private List<LigneOrdonnanceResponseDTO> lignes;
}
