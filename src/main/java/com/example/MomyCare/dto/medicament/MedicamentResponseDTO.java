package com.example.MomyCare.dto.medicament;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentResponseDTO {
    private Long codeMedicament;
    private String nomMedicament;
}
