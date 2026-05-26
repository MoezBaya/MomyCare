package com.example.MomyCare.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.OrdonnanceRepository;
import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdonnanceService {

    private final OrdonnanceRepository   ordonnanceRepository;
    private final ConsultationRepository consultationRepository;
    private final MedicamentService      medicamentService;
    private final OrdonnanceMapper       ordonnanceMapper;

    // ── CREATE ───────────────────────────────────────────────────────

    @Transactional
    public OrdonnanceResponseDTO createOrdonnance(Long consultationId,
                                                  OrdonnanceRequestDTO dto,
                                                  Authentication auth) {
        UserDetailsImpl currentUser = (UserDetailsImpl) auth.getPrincipal();
        log.info("Création ordonnance — consultation {} par {}",
                consultationId, currentUser.getUsername());

        Consultation consultation = findConsultationOrThrow(consultationId);

        Ordonnance ordonnance = ordonnanceMapper.toEntity(dto);
        ordonnance.setConsultation(consultation);

        if (dto.getLignes() != null) {
            dto.getLignes().forEach(ligneDTO -> {
                Medicament medicament = medicamentService.findOrThrow(ligneDTO.getMedicamentId());
                LigneOrdonnance ligne = ordonnanceMapper.toLigneEntity(ligneDTO);
                ligne.setMedicament(medicament);
                ordonnance.ajouterLigneOrdonance(ligne);
            });
        }

        Ordonnance saved = ordonnanceRepository.save(ordonnance);
        log.info("Ordonnance {} créée avec succès", saved.getIdOrdonnance());
        return ordonnanceMapper.toResponseDTO(saved);
    }

    // ── GET ONE ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public OrdonnanceResponseDTO getOrdonnanceById(Long consultationId, Long ordonnanceId) {
        return ordonnanceMapper.toResponseDTO(findOrThrow(consultationId, ordonnanceId));
    }

    // ── GET ALL by consultation ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<OrdonnanceResponseDTO> getOrdonnancesByConsultation(Long consultationId) {
        findConsultationOrThrow(consultationId); // vérifie existence
        return ordonnanceMapper.toResponseDTOList(
                ordonnanceRepository.findByConsultation_IdConsultation(consultationId));
    }

    // ── DELETE ───────────────────────────────────────────────────────

    @Transactional
    public void deleteOrdonnance(Long consultationId, Long ordonnanceId, Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        log.info("Suppression ordonnance {} par {}", ordonnanceId, user.getUsername());

        Ordonnance ordonnance = findOrThrow(consultationId, ordonnanceId);
        ordonnance.getConsultation().supprimerOrdonnance(ordonnance); // méthode utilitaire
        ordonnanceRepository.delete(ordonnance);
        log.info("Ordonnance {} supprimée", ordonnanceId);
    }

    // ── helpers ──────────────────────────────────────────────────────

    // public → réutilisé par LigneOrdonnanceService avec un seul param
    public Ordonnance findOrThrow(Long ordonnanceId) {
        return ordonnanceRepository.findById(ordonnanceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordonnance introuvable avec l'id : " + ordonnanceId));
    }

    // vérifie aussi que l'ordonnance appartient bien à la consultation
    public Ordonnance findOrThrow(Long consultationId, Long ordonnanceId) {
        Ordonnance ordonnance = findOrThrow(ordonnanceId);

        if (!ordonnance.getConsultation().getIdConsultation().equals(consultationId)) {
            throw new ResourceNotFoundException(
                    "Ordonnance " + ordonnanceId +
                            " n'appartient pas à la consultation " + consultationId);
        }
        return ordonnance;
    }

    private Consultation findConsultationOrThrow(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consultation introuvable avec l'id : " + consultationId));
    }
}