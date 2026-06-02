package com.example.MomyCare.dto.rdv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RdvExistanteRequestDTO {

    @NotNull(message = "L'ID de la patiente est obligatoire")
    private Long patienteId;

    @NotNull(message = "Les données du rendez-vous sont obligatoires")
    @Valid
    private RendezVousRequestDTO rendezVous;
}
