package com.example.MomyCare.dto.rdv;

import com.example.MomyCare.model.MotifRendezVous;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVousRequestDTO {

    @NotNull(message = "L'ID du gynécologue est obligatoire")
    private Long gynecologueId;

    @NotNull(message = "La date est obligatoire")
    @Future(message = "La date doit être dans le futur")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dateRendezVous;

    @NotNull(message = "Le motif est obligatoire")
    private MotifRendezVous motif;
}
