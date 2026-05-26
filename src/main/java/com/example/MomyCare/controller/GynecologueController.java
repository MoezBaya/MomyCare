package com.example.MomyCare.controller;

import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.service.GynecologueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gynecologues")
@RequiredArgsConstructor
public class GynecologueController {

    private final GynecologueService gynecologueService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public GynecologueResponseDTO getMyProfile(Authentication auth) {
        return gynecologueService.getMyProfile(auth);
    }

    @PutMapping("/me")
    @PreAuthorize(("hasRole('GYNECOLOGUE')"))
    public GynecologueResponseDTO updateGynecologue(Authentication auth ,
                                                  @RequestBody GynecologueSignupRequest updateGynecologueDTO) {
        return gynecologueService.updateGyneco(auth, updateGynecologueDTO );
    }
    @GetMapping("/patientes")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public List<PatienteResponseDTO> getMyPatients(Authentication auth) {
        return gynecologueService.getMyPatients(auth);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GynecologueResponseDTO> getGynecologue(@PathVariable Long id) {
        GynecologueResponseDTO gynecologue  = gynecologueService.getGynecologue(id);
        return ResponseEntity.status(HttpStatus.OK).body(gynecologue);
    }
    @GetMapping
    public ResponseEntity<List<GynecologueResponseDTO>> getAllGynecologues() {
        return ResponseEntity.ok(gynecologueService.getAllGynecologues());
    }
}
