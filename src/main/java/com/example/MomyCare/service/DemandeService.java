package com.example.MomyCare.service;

import com.example.MomyCare.dao.DemandeRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dto.demande.DemandeRequestDTO;
import com.example.MomyCare.dto.demande.DemandeResponseDTO;
import com.example.MomyCare.mapper.DemandeMapper;
import com.example.MomyCare.model.Demande;
import com.example.MomyCare.model.DemandeStatus;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.security.service.UserDetailsImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final PatienteRepository patienteRepository;
    private final GynecologueRepository gynecologueRepository;
    private final DemandeMapper mapper;

    // ======================
    // SEND DEMANDE
    // ======================
    public void envoyerDemande(Authentication auth, DemandeRequestDTO dto) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Patiente patiente = patienteRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patiente not found"));

        Gynecologue gyneco = gynecologueRepository.findById(dto.getGynecologueId())
                .orElseThrow(() -> new RuntimeException("Gynecologue not found"));

        if (patiente.getGynecologue() != null) {
            throw new RuntimeException("Patiente already assigned");
        }

        if (demandeRepository.existsByPatienteIdAndGynecologueId(
                patiente.getId(), gyneco.getId())) {
            throw new RuntimeException("Demande already exists");
        }

        Demande demande = Demande.builder()
                .patiente(patiente)
                .gynecologue(gyneco)
                .status(DemandeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        demandeRepository.save(demande);
    }

    // ======================
    // ACCEPT DEMANDE
    // ======================
    public void accepterDemande(Authentication auth, Long demandeId) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Gynecologue gyneco = gynecologueRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Gynecologue not found"));

        Demande demande = demandeRepository.findByIdAndGynecologueId(demandeId, gyneco.getId())
                .orElseThrow(() -> new RuntimeException("Unauthorized or not found"));

        if (demande.getStatus() != DemandeStatus.PENDING) {
            throw new RuntimeException("Already processed");
        }

        // ACCEPT
        demande.setStatus(DemandeStatus.ACCEPTED);

        Patiente patiente = demande.getPatiente();
        patiente.setGynecologue(gyneco);

        patienteRepository.save(patiente);
        demandeRepository.save(demande);
    }

    // ======================
    // REJECT DEMANDE
    // ======================
    public void refuserDemande(Authentication auth, Long demandeId) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Gynecologue gyneco = gynecologueRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Gynecologue not found"));

        Demande demande = demandeRepository.findByIdAndGynecologueId(demandeId, gyneco.getId())
                .orElseThrow(() -> new RuntimeException("Unauthorized or not found"));

        if (demande.getStatus() != DemandeStatus.PENDING) {
            throw new RuntimeException("Already processed");
        }

        demande.setStatus(DemandeStatus.REJECTED);
    }

    // ======================
    // GET DEMANDES
    // ======================
    @Transactional
    public List<DemandeResponseDTO> getDemandesGyneco(Authentication auth) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Gynecologue gyneco = gynecologueRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Gynecologue not found"));

        return mapper.toDTOList(
                demandeRepository.findByGynecologueIdAndStatus(
                        gyneco.getId(),
                        DemandeStatus.PENDING
                )
        );
    }
}