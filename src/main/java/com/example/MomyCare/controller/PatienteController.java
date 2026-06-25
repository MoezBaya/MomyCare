package com.example.MomyCare.controller;

import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteUpdateRequest;
import com.example.MomyCare.service.PatienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patientes")
@RequiredArgsConstructor
public class PatienteController {

    private final PatienteService patienteService;

    //  Gynécologue - Voir toutes ses patientes
    @GetMapping("/mes-patientes")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<List<PatienteResponseDTO>> getMesPatientes() {
        return ResponseEntity.ok(patienteService.getMesPatientes());
    }



    //  Patiente - Voir son propre profil
    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<PatienteResponseDTO> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(patienteService.getMyProfile(authentication));
    }

    // ✅ Patiente - Modifier son profil
    @PutMapping("/me")
    @PreAuthorize("hasRole('PATIENTE')")
    public ResponseEntity<PatienteResponseDTO> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody PatienteUpdateRequest request
    ) {
        return ResponseEntity.ok(patienteService.updateMyProfile(authentication, request));
    }
}