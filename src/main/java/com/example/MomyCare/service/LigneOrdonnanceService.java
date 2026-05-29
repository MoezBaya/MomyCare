package com.example.MomyCare.service;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.LigneOrdonnanceRepository;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LigneOrdonnanceService {

    private final LigneOrdonnanceRepository ligneRepo;
    private final OrdonnanceService ordonnanceService;
    private final MedicamentService medicamentService;
    private final OrdonnanceMapper mapper;
    private final AuthorizationService authService;

    // ================= GET ALL =================

    @Transactional(readOnly = true)
    public List<LigneOrdonnanceResponseDTO> getLignesByOrdonnance(
            Long ordonnanceId
    ) {

        Ordonnance ordonnance = ordonnanceService.findOrThrow(ordonnanceId);

        return mapper.toLigneResponseDTOList(
                ligneRepo.findByOrdonnanceIdOrdonnance(ordonnanceId)
        );
    }

    // ================= GET BY ID =================

    @Transactional(readOnly = true)
    public LigneOrdonnanceResponseDTO getLigneById(
            Long ordonnanceId,
            Long ligneId
    ) {

        LigneOrdonnance ligne =
                findLigneOrThrow(ordonnanceId, ligneId);

        return mapper.toLigneResponseDTO(ligne);
    }

    // ================= CREATE =================

    public LigneOrdonnanceResponseDTO addLigne(
            Long ordonnanceId,
            LigneOrdonnanceRequestDTO dto,
            Authentication auth
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        Ordonnance ordonnance =
                ordonnanceService.findOrThrow(ordonnanceId);

        authService.getConsultationIfAuthorized(
                ordonnance.getConsultation().getIdConsultation(),
                gyneco.getId()
        );

        Medicament medicament =
                medicamentService.findOrThrow(dto.getMedicamentId());

        LigneOrdonnance ligne =
                mapper.toLigneEntity(dto);

        ligne.setMedicament(medicament);
        ligne.setOrdonnance(ordonnance);

        LigneOrdonnance saved =
                ligneRepo.save(ligne);

        log.info(
                "Ligne {} ajoutée à l'ordonnance {}",
                saved.getIdLigneOrdonnance(),
                ordonnanceId
        );

        return mapper.toLigneResponseDTO(saved);
    }

    // ================= UPDATE =================

    public LigneOrdonnanceResponseDTO updateLigne(
            Long ordonnanceId,
            Long ligneId,
            LigneOrdonnanceRequestDTO dto,
            Authentication auth
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        LigneOrdonnance ligne =
                findLigneOrThrow(ordonnanceId, ligneId);

        authService.getConsultationIfAuthorized(
                ligne.getOrdonnance().getConsultation().getIdConsultation(),
                gyneco.getId()
        );

        Medicament medicament =
                medicamentService.findOrThrow(dto.getMedicamentId());

        mapper.updateLigneFromDto(dto, ligne);
        ligne.setMedicament(medicament);

        LigneOrdonnance updated =
                ligneRepo.save(ligne);

        log.info("Ligne {} mise à jour", ligneId);

        return mapper.toLigneResponseDTO(updated);
    }

    // ================= DELETE =================

    public void deleteLigne(
            Long ordonnanceId,
            Long ligneId,
            Authentication auth
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        LigneOrdonnance ligne =
                findLigneOrThrow(ordonnanceId, ligneId);

        authService.getConsultationIfAuthorized(
                ligne.getOrdonnance().getConsultation().getIdConsultation(),
                gyneco.getId()
        );

        ligneRepo.delete(ligne);

        log.info(
                "Ligne {} supprimée de l'ordonnance {}",
                ligneId,
                ordonnanceId
        );
    }

    // ================= HELPERS =================

    private LigneOrdonnance findLigneOrThrow(
            Long ordonnanceId,
            Long ligneId
    ) {

        LigneOrdonnance ligne =
                ligneRepo.findById(ligneId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Ligne introuvable avec l'id : " + ligneId
                                )
                        );

        if (!ligne.getOrdonnance()
                .getIdOrdonnance()
                .equals(ordonnanceId)) {

            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Cette ligne n'appartient pas à cette ordonnance"
            );
        }

        return ligne;
    }
}