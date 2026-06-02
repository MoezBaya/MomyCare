package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.PatienteMapper;
import com.example.MomyCare.mapper.UserMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.PatienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatienteServiceImpl implements PatienteService {

    private final PatienteRepository patienteRepository;
    private final RelationRepository relationRepository;
    private final PatienteMapper patienteMapper;
    private final UserMapper userMapper;
    private final SecurityContextService security;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<PatienteResponseDTO> getMesPatientes() {
        Gynecologue gyneco = security.getGyneco();
        List<Relation> relations = relationRepository.findByGynecologue_IdAndStatus(
                gyneco.getId(), StatutRelation.ACTIVE);
        return relations.stream()
                .map(Relation::getPatiente)
                .map(patienteMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatienteResponseDTO getMyProfile(Authentication auth) {
        Patiente patiente = security.getPatiente();
        return patienteMapper.toDto(patiente);
    }

    @Transactional
    public PatienteResponseDTO updateMyProfile(Authentication auth, PatienteSignupRequest dto) {
        Patiente patiente = security.getPatiente();
        patienteMapper.updateFromDto(dto, patiente);
        userMapper.updateUserFromPatienteDto(dto, patiente.getUser());
        patienteRepository.save(patiente);
        return patienteMapper.toDto(patiente);
    }
}