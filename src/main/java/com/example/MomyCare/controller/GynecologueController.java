package com.example.MomyCare.controller;

import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.service.GynecologueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GynecologueController {

    private final GynecologueService gynecologueService;

    @GetMapping("/gyneco/me")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public GynecologueResponseDTO getMyProfile(Authentication auth) {
        return gynecologueService.getMyProfile(auth);
    }

    @PutMapping("/gyneco/me")
    @PreAuthorize(("hasRole('GYNECOLOGUE')"))
    public GynecologueResponseDTO updateGynecologue(Authentication auth ,
                                                  @RequestBody GynecologueSignupRequest updateGynecologueDTO) {
        return gynecologueService.updateGyneco(auth, updateGynecologueDTO );
    }
    @GetMapping("/gyneco/patientes")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public List<PatienteResponseDTO> getMyPatients(Authentication auth) {
        return gynecologueService.getMyPatients(auth);
    }
}
