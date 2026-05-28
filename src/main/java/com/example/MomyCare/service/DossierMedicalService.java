package com.example.MomyCare.service;

import com.example.MomyCare.dao.DossierMedicaleRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import com.example.MomyCare.mapper.DossierMedicalMapper;
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
@Transactional
public class DossierMedicalService {

    private final DossierMedicaleRepository dossierRepo;
    private final PatienteRepository patienteRepo;
    private final GynecologueRepository gynecologueRepo;
    private final RelationRepository relationRepo;
    private final DossierMedicalMapper mapper;

    @Transactional
    public DossierMedicaleResponseDTO createForPatiente(
            Authentication auth, Long patienteId, CreateDossierMedicaleDTO dto) {

        Gynecologue gyneco = getGyneco(auth);
        checkRelationActive(patienteId, gyneco.getId());

        Patiente patiente = patienteRepo.findById(patienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));

        if (patiente.getDossierMedicale() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Dossier déjà existant pour cette patiente");
        }

        DossierMedicale dossier = mapper.toEntity(dto);
        dossier.setPatiente(patiente);
        dossier.setDerniereModificationPar(gyneco);

        return mapper.toDto(dossierRepo.save(dossier));
    }


    @Transactional(readOnly= true)
    public DossierMedicaleResponseDTO getByPatienteId(
            Authentication auth, Long patienteId) {

        Gynecologue gyneco = getGyneco(auth);
        checkRelationActive(patienteId, gyneco.getId());

        Patiente patiente = patienteRepo.findById(patienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));

        if (patiente.getDossierMedicale() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Aucun dossier pour cette patiente");
        }

        return mapper.toDto(patiente.getDossierMedicale());
    }


    @Transactional
    public DossierMedicaleResponseDTO update(
            Authentication auth, Long patienteId, CreateDossierMedicaleDTO dto) {

        Gynecologue gyneco = getGyneco(auth);
        checkRelationActive(patienteId, gyneco.getId());

        Patiente patiente = patienteRepo.findById(patienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));

        DossierMedicale dossier = patiente.getDossierMedicale();

        if (dossier == null) {
            dossier = mapper.toEntity(dto);
            dossier.setPatiente(patiente);
        } else {
            mapper.updateEntityFromDto(dto, dossier);
        }

        dossier.setDerniereModificationPar(gyneco);

        return mapper.toDto(dossierRepo.save(dossier));
    }


    @Transactional(readOnly = true)
    public DossierMedicaleResponseDTO getMyDossier(Authentication auth) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Patiente patiente = patienteRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));


        if (patiente.getDossierMedicale() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Aucun dossier médical encore créé");
        }

        return mapper.toDto(patiente.getDossierMedicale());
    }

    private Gynecologue getGyneco(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return gynecologueRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));
    }


    private void checkRelationActive(Long patienteId, Long gynecologueId) {

        relationRepo
                .findByPatiente_IdAndGynecologue_IdAndStatus(patienteId, gynecologueId, StatutRelation.ACTIVE)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.FORBIDDEN, "Aucune relation active avec cette patiente"));
    }
}