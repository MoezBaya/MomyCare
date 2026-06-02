package com.example.MomyCare.service;

import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PatienteService {
    List<PatienteResponseDTO> getMesPatientes();

    PatienteResponseDTO getMyProfile(Authentication authentication);

    PatienteResponseDTO updateMyProfile(Authentication authentication, @Valid PatienteSignupRequest request);
}
