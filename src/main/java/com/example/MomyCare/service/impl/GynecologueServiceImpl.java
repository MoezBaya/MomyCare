package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.GynecologueMapper;
import com.example.MomyCare.mapper.UserMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.GynecologueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GynecologueServiceImpl implements GynecologueService {

    private final GynecologueRepository gynecologueRepository;
    private final GynecologueMapper gynecologueMapper;
    private final UserMapper userMapper;
    private final SecurityContextService security;

    @Transactional(readOnly = true)
    public GynecologueResponseDTO getMyProfile(Authentication auth) {
        Gynecologue gyneco = security.getGyneco();
        return gynecologueMapper.toDto(gyneco);
    }

    public GynecologueResponseDTO updateGyneco(Authentication auth, GynecologueSignupRequest dto) {
        Gynecologue gyneco = security.getGyneco();
        gynecologueMapper.updateFromDto(dto, gyneco);
        userMapper.updateUserFromGynecoDto(dto, gyneco.getUser());
        gynecologueRepository.save(gyneco);
        return gynecologueMapper.toDto(gyneco);
    }

    @Transactional(readOnly = true)
    public GynecologueResponseDTO getGynecologue(Long id) {
        Gynecologue gyneco = gynecologueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gynécologue non trouvé"));
        return gynecologueMapper.toDto(gyneco);
    }

    @Transactional(readOnly = true)
    public List<GynecologueResponseDTO> getAllGynecologues() {
        return gynecologueMapper.toDtosList(gynecologueRepository.findAll());
    }
}