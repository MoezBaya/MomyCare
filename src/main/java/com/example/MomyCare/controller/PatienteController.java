package com.example.MomyCare.controller;

import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.service.PatienteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PatienteController {

    private final PatienteService patienteService;

    @GetMapping("/patiente/me")
    public PatienteResponseDTO gtMyProfile(Authentication auth) {
        return patienteService.getMyProfile(auth);
    }

    @PutMapping("/patiente/me")
    public PatienteResponseDTO updateMyProfile(Authentication auth ,
                                               @RequestBody PatienteSignupRequest dto) {
        return patienteService.updateMyProfile(auth ,  dto) ;
    }
}
