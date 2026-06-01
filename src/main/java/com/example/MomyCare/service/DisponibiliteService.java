package com.example.MomyCare.service;


import com.example.MomyCare.dao.DisponibiliteRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.exception.DisponibiliteNotFoundException;
import com.example.MomyCare.mapper.DisponibiliteMapper;
import com.example.MomyCare.model.Disponibilite;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.validation.DisponibiliteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibiliteService {

    private final DisponibiliteRepository  disponibiliteRepository;
    private final GynecologueRepository    gynecologueRepository;
    private final DisponibiliteMapper      disponibiliteMapper;
    private final DisponibiliteValidator disponibiliteValidator;

    // ── Lecture ──────────────────────────────────────────────────────────────

    /**
     * Toutes les disponibilités d'un gynécologue.
     * Endpoint : GET /api/disponibilites/gyneco/{gynecologueId}
     * NOM INCHANGÉ.
     */
    public List<DisponibiliteDTO> getDisponibilitesParGyneco(Long gynecologueId) {
        return disponibiliteMapper.toDtoList(
                disponibiliteRepository.findByGynecologueId(gynecologueId)
        );
    }

    /**
     * Disponibilités du gynécologue connecté.
     * Endpoint : GET /api/disponibilites/mes-disponibilites
     * NOM INCHANGÉ.
     */
    public List<DisponibiliteDTO> getMesDisponibilites(Long gynecologueId) {
        return getDisponibilitesParGyneco(gynecologueId);
    }

    /**
     * Récupère une disponibilité par son ID.
     */
    public DisponibiliteDTO getDisponibiliteById(Long id) {
        return disponibiliteMapper.toDto(findOrThrow(id));
    }

    // ── Écriture ─────────────────────────────────────────────────────────────

    @Transactional
    public DisponibiliteDTO creerDisponibilite(Long gynecologueId,
                                                       DisponibiliteRequestDTO dto) {
        // 1. Validations métier
        disponibiliteValidator.validerCoherenceHeures(dto);
        disponibiliteValidator.validerUnicite(gynecologueId, dto);
        disponibiliteValidator.validerAbsenceChevauchement(gynecologueId, dto);

        // 2. Construction de l'entité
        Gynecologue gynecologue = gynecologueRepository.findById(gynecologueId)
                .orElseThrow(() -> new DisponibiliteNotFoundException(
                        "Gynécologue introuvable : " + gynecologueId));

        Disponibilite entity = disponibiliteMapper.toEntity(dto);
        entity.setGynecologue(gynecologue);

        // 3. Persistance
        return disponibiliteMapper.toDto(disponibiliteRepository.save(entity));
    }

    /**
     * Met à jour une disponibilité existante.
     * Vérifie que le gynécologue connecté en est le propriétaire.
     */
    @Transactional
    public DisponibiliteDTO mettreAJourDisponibilite(Long id,
                                                             Long gynecologueId,
                                                             DisponibiliteRequestDTO dto) {
        Disponibilite existante = findOrThrow(id);

        // Contrôle de propriété
        if (!existante.getGynecologue().getId().equals(gynecologueId)) {
            throw new AccessDeniedException("Cette disponibilité ne vous appartient pas.");
        }

        // Validations métier (on exclut l'entité en cours de modification du check de doublon)
        disponibiliteValidator.validerCoherenceHeures(dto);

        // Mise à jour via MapStruct (updateEntityFromDto)
        disponibiliteMapper.updateEntityFromDto(dto, existante);

        return disponibiliteMapper.toDto(disponibiliteRepository.save(existante));
    }

    /**
     * Supprime une disponibilité.
     * Endpoint : DELETE /api/disponibilites/{id}
     * NOM INCHANGÉ (deleteDisponibilite).
     */
    @Transactional
    public void deleteDisponibilite(Long id, Long gynecologueId) {
        Disponibilite existante = findOrThrow(id);

        if (!existante.getGynecologue().getId().equals(gynecologueId)) {
            throw new AccessDeniedException("Cette disponibilité ne vous appartient pas.");
        }

        disponibiliteRepository.deleteById(id);
    }

    // ── Helpers privés ───────────────────────────────────────────────────────

    private Disponibilite findOrThrow(Long id) {
        return disponibiliteRepository.findById(id)
                .orElseThrow(() -> new DisponibiliteNotFoundException(id));
    }
}
