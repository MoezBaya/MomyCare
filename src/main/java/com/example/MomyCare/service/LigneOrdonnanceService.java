package com.example.MomyCare.service;

import com.example.MomyCare.dao.LigneOrdonnanceRepository;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.LigneOrdonnance;
import com.example.MomyCare.model.Medicament;
import com.example.MomyCare.model.Ordonnance;
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
public class LigneOrdonnanceService {

    private final LigneOrdonnanceRepository ligneOrdonnanceRepository;
    private final OrdonnanceService         ordonnanceService;
    private final MedicamentService         medicamentService;
    private final OrdonnanceMapper          ordonnanceMapper;

    @Transactional(readOnly = true)
    public List<LigneOrdonnanceResponseDTO> getLignesByOrdonnance(Long ordonnanceId) {
        ordonnanceService.findOrThrow( ordonnanceId);
        return ordonnanceMapper.toLigneResponseDTOList(
                ligneOrdonnanceRepository.findByOrdonnanceIdOrdonnance(ordonnanceId));
    }

    @Transactional(readOnly = true)
    public LigneOrdonnanceResponseDTO getLigneById(Long ordonnanceId, Long ligneId) {
        return ordonnanceMapper.toLigneResponseDTO(findLigneOrThrow(ordonnanceId, ligneId));
    }

    @Transactional
    public LigneOrdonnanceResponseDTO addLigne(Long ordonnanceId,
                                               LigneOrdonnanceRequestDTO dto,
                                               Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        Ordonnance ordonnance = ordonnanceService.findOrThrow(ordonnanceId);
        Medicament medicament = medicamentService.findOrThrow(dto.getMedicamentId());

        LigneOrdonnance ligne = ordonnanceMapper.toLigneEntity(dto);
        ligne.setMedicament(medicament);
        ordonnance.ajouterLigneOrdonance(ligne);

        LigneOrdonnance saved = ligneOrdonnanceRepository.save(ligne);
        log.info("Ligne {} ajoutée à l'ordonnance {}", saved.getIdLigneOrdonnance(), ordonnanceId);
        return ordonnanceMapper.toLigneResponseDTO(saved);
    }

    @Transactional
    public LigneOrdonnanceResponseDTO updateLigne(Long ordonnanceId,
                                                  Long ligneId,
                                                  LigneOrdonnanceRequestDTO dto,
                                                  Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        LigneOrdonnance ligne = findLigneOrThrow(ordonnanceId, ligneId);
        Medicament medicament = medicamentService.findOrThrow(dto.getMedicamentId());

        ordonnanceMapper.updateLigneFromDto(dto, ligne);
        ligne.setMedicament(medicament);

        LigneOrdonnance updated = ligneOrdonnanceRepository.save(ligne);
        log.info("Ligne {} mise à jour", ligneId);
        return ordonnanceMapper.toLigneResponseDTO(updated);
    }

    @Transactional
    public void deleteLigne(Long ordonnanceId, Long ligneId , Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        LigneOrdonnance ligne = findLigneOrThrow(ordonnanceId, ligneId);
        ligne.getOrdonnance().supprimerLigneOrdonance(ligne);
        ligneOrdonnanceRepository.delete(ligne);
        log.info("Ligne {} supprimée de l'ordonnance {}", ligneId, ordonnanceId);
    }

    // ── helper ───────────────────────────────────────────────────────

    private LigneOrdonnance findLigneOrThrow(Long ordonnanceId, Long ligneId) {
        LigneOrdonnance ligne = ligneOrdonnanceRepository.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ligne introuvable avec l'id : " + ligneId));

        if (!ligne.getOrdonnance().getIdOrdonnance().equals(ordonnanceId))
            throw new ResourceNotFoundException(
                    "Ligne " + ligneId + " n'appartient pas à l'ordonnance " + ordonnanceId);

        return ligne;
    }
}