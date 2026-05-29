package com.example.MomyCare.service;

import com.example.MomyCare.dao.DossierMedicaleRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import com.example.MomyCare.mapper.DossierMedicalMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.AccessControlService;
import com.example.MomyCare.security.service.SecurityContextService;
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
@Transactional
public class DossierMedicalService {

    private final DossierMedicaleRepository dossierRepo;
    private final PatienteRepository patienteRepo;
    private final DossierMedicalMapper mapper;

    private final SecurityContextService security;
    private final AccessControlService access;

    public DossierMedicaleResponseDTO createForPatiente(
            Authentication auth,
            Long patienteId,
            CreateDossierMedicaleDTO dto
    ) {

        Gynecologue gyneco = security.getGyneco(auth);
        access.checkRelationActive(patienteId, gyneco.getId());
        Patiente patiente = findPatienteOrThrow(patienteId);

        if (dossierRepo.existsByPatiente_Id(patienteId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Dossier déjà existant pour cette patiente"
            );
        }

        DossierMedicale dossier = mapper.toEntity(dto);
        dossier.setPatiente(patiente);
        dossier.setDerniereModificationPar(gyneco);

        return mapper.toDto(dossierRepo.save(dossier));
    }

    @Transactional(readOnly = true)
    public DossierMedicaleResponseDTO getByPatienteId(
            Authentication auth,
            Long patienteId
    ) {

        Gynecologue gyneco = security.getGyneco(auth);
        access.checkRelationActive(patienteId, gyneco.getId());
        DossierMedicale dossier = findDossierOrThrow(patienteId);

        return mapper.toDto(dossier);
    }

    // ─────────────────────────────────────────────
    // UPDATE dossier
    // ─────────────────────────────────────────────
    public DossierMedicaleResponseDTO update(
            Authentication auth,
            Long patienteId,
            CreateDossierMedicaleDTO dto
    ) {

        Gynecologue gyneco = security.getGyneco(auth);

        access.checkRelationActive(patienteId, gyneco.getId());

        DossierMedicale dossier = dossierRepo.findByPatiente_Id(patienteId)
                .orElseGet(() -> {
                    Patiente p = findPatienteOrThrow(patienteId);
                    DossierMedicale d = mapper.toEntity(dto);
                    d.setPatiente(p);
                    return d;
                });

        mapper.updateEntityFromDto(dto, dossier);
        dossier.setDerniereModificationPar(gyneco);

        return mapper.toDto(dossierRepo.save(dossier));
    }

    // ─────────────────────────────────────────────
    // PATIENT OWN DOSSIER
    // ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public DossierMedicaleResponseDTO getMyDossier(Authentication auth) {

        Patiente patiente = security.getPatiente(auth);

        DossierMedicale dossier = findDossierOrThrow(patiente.getId());

        return mapper.toDto(dossier);
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private Patiente findPatienteOrThrow(Long id) {
        return patienteRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Patiente non trouvée"
                        )
                );
    }

    private DossierMedicale findDossierOrThrow(Long patienteId) {
        return dossierRepo.findByPatiente_Id(patienteId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Aucun dossier médical trouvé"
                        )
                );
    }
}