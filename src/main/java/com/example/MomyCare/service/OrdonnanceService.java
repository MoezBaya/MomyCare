package com.example.MomyCare.service;

import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface OrdonnanceService {
    OrdonnanceResponseDTO createOrdonnance(Long consultationId, @Valid OrdonnanceRequestDTO dto);

    OrdonnanceResponseDTO getOrdonnanceById(Long consultationId, Long ordonnanceId);

    List<OrdonnanceResponseDTO> getOrdonnancesByConsultation(Long consultationId);

    void deleteOrdonnance(Long consultationId, Long ordonnanceId);
}
