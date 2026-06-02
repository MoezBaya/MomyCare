package com.example.MomyCare.service;

import com.example.MomyCare.dto.consultation.ConsultationRequestDTO;
import com.example.MomyCare.dto.consultation.ConsultationResponseDTO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ConsultationService {
    ConsultationResponseDTO addConsultation(Authentication auth, @Valid ConsultationRequestDTO dto);

    List<ConsultationResponseDTO> getConsultationsByPatiente(Authentication auth, Long patienteId);

    List<ConsultationResponseDTO> getMesConsultations(Authentication auth);
}
