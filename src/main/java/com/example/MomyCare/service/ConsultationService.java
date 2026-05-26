package com.example.MomyCare.service;

import com.example.MomyCare.dao.*;
import com.example.MomyCare.dto.consultation.*;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.ConsultationMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final GynecologueRepository  gynecologueRepository;
    private final PatienteRepository     patienteRepository;
    private final ConsultationMapper     mapper;
    private final RelationRepository relationRepository;

    // ─── GET: toutes les consultations d'une patiente ─────────────────────────
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByPatiente(Authentication auth, Long patienteId) {
        getAuthenticatedGyneco(auth); // vérifie que c'est bien un gynéco

        Patiente patiente = findPatiente(patienteId);
        validateDossierExists(patiente);

        return mapper.toDtoList(patiente.getDossierMedicale().getConsultations());
    }

    // ─── POST: ajouter une consultation ───────────────────────────────────────
    @Transactional
    public ConsultationResponseDTO addConsultation(Authentication auth, ConsultationRequestDTO dto) {

        Gynecologue gynecologue = getAuthenticatedGyneco(auth);

        checkRelationActive(dto.getPatienteId(), gynecologue.getId());

        Patiente patiente = findPatiente(dto.getPatienteId());

        validateDossierExists(patiente);

        DossierMedicale dossier = patiente.getDossierMedicale();

        Consultation consultation = mapper.toEntity(dto);

        consultation.setDossierMedicale(dossier);

        return mapper.toDto(consultationRepository.save(consultation));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Gynecologue getAuthenticatedGyneco(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return gynecologueRepository.findByUser_Id(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));
    }

    private Patiente findPatiente(Long patienteId) {
        return patienteRepository.findById(patienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Patiente non trouvée avec l'id : " + patienteId));
    }

    private void validateDossierExists(Patiente patiente) {
        if (patiente.getDossierMedicale() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Aucun dossier médical trouvé pour cette patiente");
        }
    }

    private void checkRelationActive(Long patienteId, Long gynecologueId) {

        relationRepository
                .findByPatiente_IdAndGynecologue_IdAndStatus(
                        patienteId,
                        gynecologueId,
                        StatutRelation.ACTIVE
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "Aucune relation active avec cette patiente"
                        ));
    }
}