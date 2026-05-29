package com.example.MomyCare.service;

import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.mapper.PatienteMapper;
import com.example.MomyCare.mapper.UserMapper;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.User;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Transactional
public class PatienteService {

    private final PatienteRepository patienteRepository;
    private final UserRepository userRepository;

    private final PatienteMapper patienteMapper;
    private final UserMapper userMapper;

    private final SecurityContextService security;

    // ─────────────────────────────────────────────
    // GET MY PROFILE
    // ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public PatienteResponseDTO getMyProfile(Authentication auth) {

        Patiente patiente = security.getPatiente(auth);

        return patienteMapper.toDto(patiente);
    }

    // ─────────────────────────────────────────────
    // UPDATE MY PROFILE
    // ─────────────────────────────────────────────
    public PatienteResponseDTO updateMyProfile(
            Authentication auth,
            PatienteSignupRequest dto
    ) {

        Patiente patiente = security.getPatiente(auth);

        // update PATIENT fields
        patienteMapper.updateFromDto(dto, patiente);

        // update USER fields
        User user = patiente.getUser();

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User associé introuvable"
            );
        }

        userMapper.updateUserFromPatienteDto(dto, user);

        userRepository.save(user);
        Patiente saved = patienteRepository.save(patiente);

        return patienteMapper.toDto(saved);
    }
}