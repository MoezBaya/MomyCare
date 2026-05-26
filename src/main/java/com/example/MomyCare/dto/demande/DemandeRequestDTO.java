package com.example.MomyCare.dto.demande;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeRequestDTO {

    @NotNull
    private Long gynecologueId;
}
