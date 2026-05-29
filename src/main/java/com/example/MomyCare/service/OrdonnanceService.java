package com.example.MomyCare.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.OrdonnanceRepository;
import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.AuthorizationService;
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

    private final OrdonnanceRepository ordonnanceRepo;
    private final ConsultationRepository consultationRepo;
    private final OrdonnanceMapper mapper;
    private final AuthorizationService authService;

    // ================= CREATE =================

    public OrdonnanceResponseDTO createOrdonnance(
            Authentication auth,
            Long consultationId,
            OrdonnanceRequestDTO dto
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        Consultation consultation =
                authService.getConsultationIfAuthorized(
                        consultationId,
                        gyneco.getId()
                );

        boolean exists = ordonnanceRepo
                .existsByNumOrdonnanceAndConsultation_IdConsultation(
                        dto.getNumOrdonnance(),
                        consultationId
                );

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce numéro d'ordonnance existe déjà"
            );
        }

        Ordonnance ordonnance = mapper.toEntity(dto);
        ordonnance.setConsultation(consultation);

        return mapper.toResponseDTO(ordonnanceRepo.save(ordonnance));
    }

    // ================= GET BY ID =================

    @Transactional(readOnly = true)
    public OrdonnanceResponseDTO getOrdonnanceById(
            Long consultationId,
            Long ordonnanceId
    ) {

        Ordonnance ordonnance = findOrThrow(ordonnanceId);

        validateBelongsToConsultation(ordonnance, consultationId);

        return mapper.toResponseDTO(ordonnance);
    }

    // ================= GET ALL =================

    @Transactional(readOnly = true)
    public List<OrdonnanceResponseDTO> getOrdonnancesByConsultation(
            Authentication auth,
            Long consultationId
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        authService.getConsultationIfAuthorized(
                consultationId,
                gyneco.getId()
        );

        return mapper.toResponseDTOList(
                ordonnanceRepo.findByConsultation_IdConsultation(consultationId)
        );
    }

    // ================= DELETE =================

    public void deleteOrdonnance(
            Authentication auth,
            Long consultationId,
            Long ordonnanceId
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        Consultation consultation =
                authService.getConsultationIfAuthorized(
                        consultationId,
                        gyneco.getId()
                );

        Ordonnance ordonnance = findOrThrow(ordonnanceId);

        validateBelongsToConsultation(ordonnance, consultationId);

        if (!ordonnance.getConsultation().getIdConsultation()
                .equals(consultation.getIdConsultation())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Accès refusé"
            );
        }

        ordonnanceRepo.delete(ordonnance);
    }

    // ================= HELPERS =================

    public Ordonnance findOrThrow(Long ordonnanceId) {
        return ordonnanceRepo.findById(ordonnanceId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Ordonnance non trouvée"
                        ));
    }

    private void validateBelongsToConsultation(
            Ordonnance ordonnance,
            Long consultationId
    ) {

        if (!ordonnance.getConsultation()
                .getIdConsultation()
                .equals(consultationId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cette ordonnance n'appartient pas à cette consultation"
            );
        }
    }
}