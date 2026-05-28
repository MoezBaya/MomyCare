package com.example.MomyCare.service;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.LigneOrdonnanceRepository;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    private final LigneOrdonnanceRepository ligneOrdonnanceRepository;
    private final OrdonnanceService ordonnanceService;
    private final MedicamentService medicamentService;
    private final OrdonnanceMapper ordonnanceMapper;
    private final GynecologueRepository gynecologueRepo;

    // ================= GET ALL =================

    @Transactional(readOnly = true)
    public List<LigneOrdonnanceResponseDTO> getLignesByOrdonnance(
            Long ordonnanceId
    ) {

        ordonnanceService.findOrThrow(ordonnanceId);

        return ordonnanceMapper.toLigneResponseDTOList(
                ligneOrdonnanceRepository
                        .findByOrdonnanceIdOrdonnance(ordonnanceId)
        );
    }

    // ================= GET BY ID =================

    @Transactional(readOnly = true)
    public LigneOrdonnanceResponseDTO getLigneById(
            Long ordonnanceId,
            Long ligneId
    ) {

        return ordonnanceMapper.toLigneResponseDTO(
                findLigneOrThrow(ordonnanceId, ligneId)
        );
    }

    // ================= CREATE =================

    public LigneOrdonnanceResponseDTO addLigne(
            Long ordonnanceId,
            LigneOrdonnanceRequestDTO dto,
            Authentication auth
    ) {

        Gynecologue gyneco = getGyneco(auth);

        Ordonnance ordonnance =
                ordonnanceService.findOrThrow(ordonnanceId);

        ordonnanceService.validateConsultationAppartientAuGyneco(
                ordonnance.getConsultation(),
                gyneco
        );

        Medicament medicament =
                medicamentService.findOrThrow(
                        dto.getMedicamentId()
                );

        LigneOrdonnance ligne =
                ordonnanceMapper.toLigneEntity(dto);

        ligne.setMedicament(medicament);

        ordonnance.ajouterLigneOrdonance(ligne);

        LigneOrdonnance saved =
                ligneOrdonnanceRepository.save(ligne);

        log.info(
                "Ligne {} ajoutée à l'ordonnance {}",
                saved.getIdLigneOrdonnance(),
                ordonnanceId
        );

        return ordonnanceMapper.toLigneResponseDTO(saved);
    }

    // ================= UPDATE =================

    public LigneOrdonnanceResponseDTO updateLigne(
            Long ordonnanceId,
            Long ligneId,
            LigneOrdonnanceRequestDTO dto,
            Authentication auth
    ) {

        Gynecologue gyneco = getGyneco(auth);

        LigneOrdonnance ligne =
                findLigneOrThrow(ordonnanceId, ligneId);

        ordonnanceService.validateConsultationAppartientAuGyneco(
                ligne.getOrdonnance().getConsultation(),
                gyneco
        );

        Medicament medicament =
                medicamentService.findOrThrow(
                        dto.getMedicamentId()
                );

        ordonnanceMapper.updateLigneFromDto(dto, ligne);

        ligne.setMedicament(medicament);

        LigneOrdonnance updated =
                ligneOrdonnanceRepository.save(ligne);

        log.info("Ligne {} mise à jour", ligneId);

        return ordonnanceMapper.toLigneResponseDTO(updated);
    }

    // ================= DELETE =================

    public void deleteLigne(
            Long ordonnanceId,
            Long ligneId,
            Authentication auth
    ) {

        Gynecologue gyneco = getGyneco(auth);

        LigneOrdonnance ligne =
                findLigneOrThrow(ordonnanceId, ligneId);

        ordonnanceService.validateConsultationAppartientAuGyneco(
                ligne.getOrdonnance().getConsultation(),
                gyneco
        );

        ligne.getOrdonnance()
                .supprimerLigneOrdonance(ligne);

        ligneOrdonnanceRepository.delete(ligne);

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
                ligneOrdonnanceRepository.findById(ligneId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Ligne introuvable avec l'id : "
                                                + ligneId
                                )
                        );

        if (!ligne.getOrdonnance()
                .getIdOrdonnance()
                .equals(ordonnanceId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cette ligne n'appartient pas à cette ordonnance"
            );
        }

        return ligne;
    }

    private Gynecologue getGyneco(Authentication auth) {

        UserDetailsImpl user =
                (UserDetailsImpl) auth.getPrincipal();

        return gynecologueRepo.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Gynécologue non trouvé"
                        )
                );
    }
}