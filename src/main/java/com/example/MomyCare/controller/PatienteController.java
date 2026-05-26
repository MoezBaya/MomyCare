package com.example.MomyCare.controller;

import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.service.GynecologueService;
import com.example.MomyCare.service.PatienteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PatienteController {

    private final PatienteService patienteService;
    private final GynecologueService gynecologueService;

    @GetMapping("/patiente/me")
    public PatienteResponseDTO gtMyProfile(Authentication auth) {
        return patienteService.getMyProfile(auth);
    }

    @PutMapping("/patiente/me")
    public PatienteResponseDTO updateMyProfile(Authentication auth ,
                                               @RequestBody PatienteSignupRequest dto) {
        return patienteService.updateMyProfile(auth ,  dto) ;
    }

    @GetMapping("/gyneco/{id}")
    public ResponseEntity<GynecologueResponseDTO> getGynecologue(@PathVariable Long id) {
        GynecologueResponseDTO gynecologue  = patienteService.getGynecologue(id);
        return ResponseEntity.status(HttpStatus.OK).body(gynecologue);
    }
}
