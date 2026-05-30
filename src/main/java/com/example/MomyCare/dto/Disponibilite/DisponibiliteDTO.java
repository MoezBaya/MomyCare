package com.example.MomyCare.dto.Disponibilite;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteDTO {

    private Long id;


    private LocalDateTime dateTime;
    private boolean disponible;

    private Long gynecologueId;
}
