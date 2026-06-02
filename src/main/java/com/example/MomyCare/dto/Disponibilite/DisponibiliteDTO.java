package com.example.MomyCare.dto.Disponibilite;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteDTO {

    private Long id;
    private DayOfWeek jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Long gynecologueId ;

}
