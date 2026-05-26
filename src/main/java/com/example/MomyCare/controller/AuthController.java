package com.example.MomyCare.controller;

import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.security.request.LoginRequest;
import com.example.MomyCare.security.request.SignupRequest;
import com.example.MomyCare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/patiente")
    public ResponseEntity<?> registerPatiente(@Valid @RequestBody PatienteSignupRequest req) {
        return authService.registerPatiente(req);
    }

    @PostMapping("/register/gynecologue")
    public ResponseEntity<?> registerGynecologue(@Valid @RequestBody GynecologueSignupRequest req) {
        return authService.registerGynecologue(req);
    }
    @GetMapping("/user")
    public ResponseEntity<?> currentUser(Authentication auth) {
        return ResponseEntity.ok(authService.getCurrentUser(auth));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}
