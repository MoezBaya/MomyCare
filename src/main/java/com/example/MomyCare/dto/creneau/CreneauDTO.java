package com.example.MomyCare.dto.creneau;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreneauDTO {
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean available;
}
