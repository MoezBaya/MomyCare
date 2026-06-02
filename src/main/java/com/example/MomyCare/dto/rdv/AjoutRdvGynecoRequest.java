package com.example.MomyCare.dto.rdv;

import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AjoutRdvGynecoRequest {
    private PatienteSignupRequest patiente;
    private RendezVousRequestDTO rendezVous;
}
