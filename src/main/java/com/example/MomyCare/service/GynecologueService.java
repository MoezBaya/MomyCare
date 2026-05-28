package com.example.MomyCare.service;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.GynecologueMapper;
import com.example.MomyCare.mapper.PatienteMapper;
import com.example.MomyCare.mapper.UserMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@jakarta.transaction.Transactional
public class GynecologueService {

    private final GynecologueRepository gynecologueRepository;
    private final PatienteRepository patienteRepository;
    private final UserRepository userRepository;
    private final GynecologueMapper gynecologueMapper;
    private final PatienteMapper patienteMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public GynecologueResponseDTO getMyProfile(Authentication auth) {
        Gynecologue gyneco = getGyneco(auth);
        return gynecologueMapper.toDto(gyneco);
    }

    @Transactional
    public GynecologueResponseDTO updateGyneco(Authentication auth,
                                               GynecologueSignupRequest dto) {
        Gynecologue gyneco = getGyneco(auth);

        gynecologueMapper.updateFromDto(dto, gyneco);
        userMapper.updateUserFromGynecoDto(dto, gyneco.getUser());
        userRepository.save(gyneco.getUser());
        gynecologueRepository.save(gyneco);

        return gynecologueMapper.toDto(gyneco);
    }

    @Transactional(readOnly = true)
    public List<PatienteResponseDTO> getMyPatients(Authentication auth) {
        Gynecologue gyneco = getGyneco(auth);
        return patienteMapper.toDTOList(
                patienteRepository.findByGynecologue_Id(gyneco.getId())
        );
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

    private Gynecologue getGyneco(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return gynecologueRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gynécologue non trouvé"));
    }
}