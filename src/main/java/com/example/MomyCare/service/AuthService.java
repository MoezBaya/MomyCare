package com.example.MomyCare.service;

import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.security.request.LoginRequest;
import com.example.MomyCare.security.request.SignupRequest;
import com.example.MomyCare.security.response.MessageResponse;
import com.example.MomyCare.security.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {

    ResponseEntity<UserInfoResponse> login(LoginRequest request);

    UserInfoResponse getCurrentUser(Authentication auth);

    ResponseEntity<MessageResponse> logout();

    ResponseEntity<?> registerGynecologue(@Valid GynecologueSignupRequest req);

    ResponseEntity<?> registerPatiente(@Valid PatienteSignupRequest req);
}
