package com.example.MomyCare.controller;

import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.service.PatienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatienteController {

    private final PatienteService patienteService;

    @GetMapping("/me")
    public PatienteResponseDTO getMyProfile(Authentication authentication) {
        return patienteService.getMyProfile(authentication);
    }

    @PutMapping("/me")
    public PatienteResponseDTO updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody PatienteSignupRequest request
    ) {
        return patienteService.updateMyProfile(authentication, request);
    }
}