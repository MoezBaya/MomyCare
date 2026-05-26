package com.example.MomyCare.service;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.GynecologueMapper;
import com.example.MomyCare.mapper.PatienteMapper;
import com.example.MomyCare.mapper.UserMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.User;
import com.example.MomyCare.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PatienteService {

    private final PatienteRepository patienteRepository;
    private final PatienteMapper patienteMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final GynecologueRepository gynecologueRepository;
    private final GynecologueMapper gynecologueMapper;

    public PatienteResponseDTO getMyProfile(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Patiente patiente = patienteRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patiente not found"));

        return patienteMapper.toDto(patiente);
    }

    public PatienteResponseDTO updateMyProfile(Authentication auth, PatienteSignupRequest dto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        Patiente patiente = patienteRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Patiente not found"));

        patienteMapper.updateFromDto(dto, patiente);

        User user = patiente.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        userMapper.updateUserFromPatienteDto(dto, user);
        userRepository.save(user);
        patienteRepository.save(patiente);
        return patienteMapper.toDto(patiente);
    }

    public GynecologueResponseDTO getGynecologue(Long id) {
        Gynecologue gynecologue = gynecologueRepository.findByUserId(id).orElseThrow(
                () -> new ResourceNotFoundException("Gynecologue not found")
        ) ;
        return gynecologueMapper.toDto(gynecologue);
    }
}