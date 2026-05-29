package com.example.MomyCare.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.DossierMedicaleRepository;
import com.example.MomyCare.dto.consultation.ConsultationRequestDTO;
import com.example.MomyCare.dto.consultation.ConsultationResponseDTO;
import com.example.MomyCare.mapper.ConsultationMapper;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.DossierMedicale;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.security.service.AccessControlService;
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
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ConsultationMapper mapper;

    private final SecurityContextService security;
    private final AccessControlService access;
    private final DossierMedicaleRepository dossierRepo;

    // ─────────────────────────────────────────────
    // GET: consultations d'une patiente (gyneco)
    // ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getConsultationsByPatiente(
            Authentication auth,
            Long patienteId
    ) {
        Gynecologue gyneco = security.getGyneco(auth);

        access.checkRelationActive(patienteId, gyneco.getId());

        return consultationRepository
                .findByDossierMedicale_Patiente_Id(patienteId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // ─────────────────────────────────────────────
    // POST: add consultation
    // ─────────────────────────────────────────────
    @Transactional
    public ConsultationResponseDTO addConsultation(
            Authentication auth,
            ConsultationRequestDTO dto
    ) {
        Gynecologue gyneco = security.getGyneco(auth);

        access.checkRelationActive(dto.getPatienteId(), gyneco.getId());

        DossierMedicale dossier = findDossierOrThrow(dto.getPatienteId());

        Consultation consultation = mapper.toEntity(dto);
        consultation.setGynecologue(gyneco);
        consultation.setDossierMedicale(dossier);

        return mapper.toDto(consultationRepository.save(consultation));
    }

    // ─────────────────────────────────────────────
    // GET: consultations du patient connecté
    // ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ConsultationResponseDTO> getMesConsultations(Authentication auth) {

        Patiente patiente = security.getPatiente(auth);

        DossierMedicale dossier = patiente.getDossierMedicale();

        if (dossier == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Aucun dossier médical trouvé"
            );
        }

        return dossier.getConsultations()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private DossierMedicale findDossierOrThrow(Long patienteId) {

        return dossierRepo.findByPatiente_Id(patienteId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Dossier médical introuvable"
                        )
                );
    }
}