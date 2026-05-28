package com.example.MomyCare.dto.rdv;

import com.example.MomyCare.model.MotifRendezVous;
import com.example.MomyCare.model.StatusRDV;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousResponseDTO {

    private Long id;
    private LocalDateTime dateRendezVous;
    private StatusRDV statusRDV;
    private MotifRendezVous motif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Patiente
    private Long patienteId;
    private String patienteNom;
    private String patientePrenom;

    // Gynécologue
    private Long gynecologueId;
    private String gynecologueNom;
    private String gynecologuePrenom;
}
