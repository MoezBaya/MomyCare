package com.example.MomyCare.service;

import com.example.MomyCare.dao.OrdonnanceRepository;
import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Ordonnance;
import com.example.MomyCare.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdonnanceService {

    private final OrdonnanceRepository   ordonnanceRepo;
    private final OrdonnanceMapper       mapper;
    private final SecurityContextService security;

    // ─── Créer ────────────────────────────────────────────────────────────────

    public OrdonnanceResponseDTO createOrdonnance(
            Authentication auth,
            Long consultationId,
            OrdonnanceRequestDTO dto
    ) {
        Gynecologue  gyneco       = security.getGyneco(auth);
        Consultation consultation = security.getConsultationIfAuthorized(consultationId, gyneco.getId());

        if (ordonnanceRepo.existsByNumOrdonnanceAndConsultation_IdConsultation(
                dto.getNumOrdonnance(), consultationId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ce numéro d'ordonnance existe déjà");
        }

        Ordonnance ordonnance = mapper.toEntity(dto);
        ordonnance.setConsultation(consultation);

        return mapper.toResponseDTO(ordonnanceRepo.save(ordonnance));
    }

    // ─── Lire par id ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public OrdonnanceResponseDTO getOrdonnanceById(Long consultationId, Long ordonnanceId) {
        Ordonnance ordonnance = findOrThrow(ordonnanceId);
        validateBelongsToConsultation(ordonnance, consultationId);
        return mapper.toResponseDTO(ordonnance);
    }

    // ─── Lire par consultation ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<OrdonnanceResponseDTO> getOrdonnancesByConsultation(
            Authentication auth,
            Long consultationId
    ) {
        Gynecologue gyneco = security.getGyneco(auth);
        security.getConsultationIfAuthorized(consultationId, gyneco.getId());

        return mapper.toResponseDTOList(
                ordonnanceRepo.findByConsultation_IdConsultation(consultationId));
    }

    // ─── Supprimer ────────────────────────────────────────────────────────────

    public void deleteOrdonnance(Authentication auth, Long consultationId, Long ordonnanceId) {
        Gynecologue gyneco = security.getGyneco(auth);
        security.getConsultationIfAuthorized(consultationId, gyneco.getId());

        Ordonnance ordonnance = findOrThrow(ordonnanceId);
        validateBelongsToConsultation(ordonnance, consultationId);

        ordonnanceRepo.delete(ordonnance);
    }

    // ─── Helpers (package-private pour LigneOrdonnanceService) ───────────────

    public Ordonnance findOrThrow(Long ordonnanceId) {
        return ordonnanceRepo.findById(ordonnanceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ordonnance non trouvée"));
    }

    private void validateBelongsToConsultation(Ordonnance ordonnance, Long consultationId) {
        if (!ordonnance.getConsultation().getIdConsultation().equals(consultationId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cette ordonnance n'appartient pas à cette consultation");
        }
    }
}