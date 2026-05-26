package com.example.MomyCare.dto.demande;

import com.example.MomyCare.model.DemandeStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandeResponseDTO {
 private Long id;
    private Long patienteId;
    private String patienteLogin;

    private Long gynecologueId;
    private String gynecologueLogin;

    private DemandeStatus status;

    private LocalDateTime createdAt;

}
