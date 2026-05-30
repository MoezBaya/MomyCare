package com.example.MomyCare.service;

import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.RelationRepository;

import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.mapper.PatienteMapper;
import com.example.MomyCare.mapper.UserMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.Relation;
import com.example.MomyCare.model.StatutRelation;
import com.example.MomyCare.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatienteService {

    private final PatienteRepository patienteRepository;
    private final RelationRepository relationRepository;
    private final PatienteMapper patienteMapper;
    private final UserMapper userMapper;
    private final SecurityContextService security;

    //  Gynécologue - Récupérer toutes ses patientes
    @Transactional(readOnly = true)
    public List<PatienteResponseDTO> getMesPatientes() {

        Gynecologue gyneco = security.getGyneco();

        List<Relation> relations =
                relationRepository.findByGynecologue_IdAndStatus(
                        gyneco.getId(),
                        StatutRelation.ACTIVE);

        return relations.stream()
                .map(Relation::getPatiente)
                .map(patienteMapper::toDto)
                .toList();
    }

    //  Patiente - Récupérer son propre profil
    @Transactional(readOnly = true)
    public PatienteResponseDTO getMyProfile(Authentication auth) {
        Patiente patiente = security.getPatiente();
        return patienteMapper.toDto(patiente);
    }

    //  Patiente - Mettre à jour son profil
    @Transactional
    public PatienteResponseDTO updateMyProfile(Authentication auth, PatienteSignupRequest dto) {
        Patiente patiente = security.getPatiente();
        patienteMapper.updateFromDto(dto, patiente);
        userMapper.updateUserFromPatienteDto(dto, patiente.getUser());
        patienteRepository.save(patiente);

        return patienteMapper.toDto(patiente);
    }
}