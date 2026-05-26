package com.example.MomyCare.dto.medicament;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentRequestDTO {

    @NotBlank
    private String nomMedicament;
}
