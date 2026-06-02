package com.example.MomyCare.dto.Disponibilite;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteRequestDTO {

    @NotNull(message = "La date est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureFin;
}