package com.example.MomyCare.controller;

import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.security.request.LoginRequest;
import com.example.MomyCare.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

@Tag(
        name = "Authentification",
        description = "Gestion de l’authentification et des utilisateurs"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Connexion utilisateur",
            description = "Authentifie un utilisateur et retourne un JWT token"
    )
    @PostMapping("/signin")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    @Operation(
            summary = "Inscription d’une patiente",
            description = "Créer un nouveau compte patiente"
    )
    @PostMapping("/register/patiente")
    public ResponseEntity<?> registerPatiente(
            @Valid @RequestBody PatienteSignupRequest request
    ) {
        return authService.registerPatiente(request);
    }

    @Operation(
            summary = "Inscription d’un gynécologue",
            description = "Créer un nouveau compte gynécologue"
    )
    @PostMapping("/register/gynecologue")
    public ResponseEntity<?> registerGynecologue(
            @Valid @RequestBody GynecologueSignupRequest request
    ) {
        return authService.registerGynecologue(request);
    }

    @Operation(
            summary = "Récupérer l’utilisateur connecté",
            description = "Retourne les informations du compte authentifié"
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user")
    public ResponseEntity<?> currentUser(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                authService.getCurrentUser(authentication)
        );
    }

    @Operation(
            summary = "Déconnexion utilisateur",
            description = "Déconnecte l’utilisateur courant"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/signout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}