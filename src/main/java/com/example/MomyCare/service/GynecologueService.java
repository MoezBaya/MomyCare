package com.example.MomyCare.service;

import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface GynecologueService {
    GynecologueResponseDTO updateGyneco(Authentication auth, GynecologueSignupRequest updateGynecologueDTO);

    GynecologueResponseDTO getMyProfile(Authentication auth);

    GynecologueResponseDTO getGynecologue(Long id);

    List<GynecologueResponseDTO> getAllGynecologues();
}
