package com.example.MomyCare.dto.RendezVous;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousDTO {

    private Long id;

    private LocalDateTime dateRendezVous;
    private String statusRDV;
    private String motif;

    private Long patienteId;
    private Long gynecologueId;
}
