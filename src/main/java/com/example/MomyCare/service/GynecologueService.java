package com.example.MomyCare.service;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.mapper.GynecologueMapper;
import com.example.MomyCare.mapper.PatienteMapper;
import com.example.MomyCare.mapper.UserMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.User;
import com.example.MomyCare.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GynecologueService {

    private final GynecologueRepository gynecologueRepository;
    private final UserRepository userRepository;
    private final GynecologueMapper gynecologueMapper;
    private final UserMapper userMapper;
    private final PatienteRepository patienteRepository;
    private final PatienteMapper patienteMapper;

    public GynecologueResponseDTO updateGyneco(Authentication auth, GynecologueSignupRequest dto) {

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        Gynecologue gynecologue = gynecologueRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Gynecologue not found"));

        gynecologueMapper.updateFromDto(dto, gynecologue);

        User user = gynecologue.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        userMapper.updateUserFromGynecoDto(dto, user);
        userRepository.save(user);
        gynecologueRepository.save(gynecologue);
        return gynecologueMapper.toDto(gynecologue);
    }

    public GynecologueResponseDTO getMyProfile(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Gynecologue gynecologue = gynecologueRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Gynecologue not found"));
        return gynecologueMapper.toDto(gynecologue);
    }

    public List<PatienteResponseDTO> getMyPatients(Authentication auth) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Gynecologue gyneco = gynecologueRepository
                .findByUserId(user.getId())
                .orElseThrow();

        List<Patiente> patiente = patienteRepository.findByGynecologueId(gyneco.getId());

        return patienteMapper.toDTOList(patiente);
    }
}