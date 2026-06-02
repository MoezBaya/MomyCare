package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.LigneOrdonnanceRepository;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import com.example.MomyCare.exception.BadRequestException;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.LigneOrdonnance;
import com.example.MomyCare.model.Medicament;
import com.example.MomyCare.model.Ordonnance;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.LigneOrdonnanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LigneOrdonnanceServiceImpl implements LigneOrdonnanceService {

    private final LigneOrdonnanceRepository ligneRepo;
    private final OrdonnanceServiceImpl         ordonnanceService;
    private final MedicamentServiceImpl         medicamentService;
    private final OrdonnanceMapper          mapper;
    private final SecurityContextService    security;

    @Transactional(readOnly = true)
    public List<LigneOrdonnanceResponseDTO> getLignesByOrdonnance(Long ordonnanceId) {
        ordonnanceService.findOrThrow(ordonnanceId);
        return mapper.toLigneResponseDTOList(ligneRepo.findByOrdonnanceIdOrdonnance(ordonnanceId));
    }

    @Transactional(readOnly = true)
    public LigneOrdonnanceResponseDTO getLigneById(Long ordonnanceId, Long ligneId) {
        return mapper.toLigneResponseDTO(findLigneOrThrow(ordonnanceId, ligneId));
    }

    public LigneOrdonnanceResponseDTO addLigne(
            Long ordonnanceId,
            LigneOrdonnanceRequestDTO dto,
            Authentication auth
    ) {
        Gynecologue gyneco     = security.getGyneco();
        Ordonnance  ordonnance = ordonnanceService.findOrThrow(ordonnanceId);
        security.getConsultationIfAuthorized(
                ordonnance.getConsultation().getIdConsultation(), gyneco.getId());

        Medicament    medicament = medicamentService.findOrThrow(dto.getMedicamentId());
        LigneOrdonnance ligne   = mapper.toLigneEntity(dto);
        ligne.setMedicament(medicament);
        ligne.setOrdonnance(ordonnance);

        LigneOrdonnance saved = ligneRepo.save(ligne);
        log.info("Ligne {} ajoutée à l'ordonnance {}", saved.getIdLigneOrdonnance(), ordonnanceId);
        return mapper.toLigneResponseDTO(saved);
    }

    public LigneOrdonnanceResponseDTO updateLigne(
            Long ordonnanceId,
            Long ligneId,
            LigneOrdonnanceRequestDTO dto,
            Authentication auth
    ) {
        Gynecologue     gyneco = security.getGyneco();
        LigneOrdonnance ligne  = findLigneOrThrow(ordonnanceId, ligneId);
        security.getConsultationIfAuthorized(
                ligne.getOrdonnance().getConsultation().getIdConsultation(), gyneco.getId());

        Medicament medicament = medicamentService.findOrThrow(dto.getMedicamentId());
        mapper.updateLigneFromDto(dto, ligne);
        ligne.setMedicament(medicament);

        LigneOrdonnance updated = ligneRepo.save(ligne);
        log.info("Ligne {} mise à jour", ligneId);
        return mapper.toLigneResponseDTO(updated);
    }

    public void deleteLigne(Long ordonnanceId, Long ligneId, Authentication auth) {
        Gynecologue     gyneco = security.getGyneco();
        LigneOrdonnance ligne  = findLigneOrThrow(ordonnanceId, ligneId);
        security.getConsultationIfAuthorized(
                ligne.getOrdonnance().getConsultation().getIdConsultation(), gyneco.getId());

        ligneRepo.delete(ligne);
        log.info("Ligne {} supprimée de l'ordonnance {}", ligneId, ordonnanceId);
    }

    private LigneOrdonnance findLigneOrThrow(Long ordonnanceId, Long ligneId) {
        LigneOrdonnance ligne = ligneRepo.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne introuvable avec l'id : " + ligneId));

        if (!ligne.getOrdonnance().getIdOrdonnance().equals(ordonnanceId)) {
            throw new BadRequestException("Cette ligne n'appartient pas à cette ordonnance");
        }
        return ligne;
    }
}