package com.example.MomyCare.service;

import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.rdv.RendezVousRequestDTO;
import com.example.MomyCare.dto.rdv.RendezVousResponseDTO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RendezVousService {
    RendezVousResponseDTO repondreRdv(Long rdvId, boolean accepter);

    @Transactional
    RendezVousResponseDTO creerRdvParGynecoExistante(
            Long patienteId,
            RendezVousRequestDTO rdvDTO);

    List<RendezVousResponseDTO> getMesRdvEnAttente();

    List<RendezVousResponseDTO> getMesRdv();

    List<RendezVousResponseDTO> getGynecoRdv();

    RendezVousResponseDTO demanderRdv(@Valid RendezVousRequestDTO dto);

    RendezVousResponseDTO creerRdvParGyneco(@Valid PatienteSignupRequest patienteSignupRequest,
                                            @Valid RendezVousRequestDTO dto);
}
