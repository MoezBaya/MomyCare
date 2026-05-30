package com.example.MomyCare.dto.Disponibilite;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteRequestDTO {

    @NotNull(message = "La date et l'heure sont obligatoires")
    @Future(message = "La disponibilité doit être dans le futur")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTime;

}


















