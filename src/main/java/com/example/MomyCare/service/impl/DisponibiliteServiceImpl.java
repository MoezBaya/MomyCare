package com.example.MomyCare.service.impl;

import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.exception.ForbiddenException;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.DisponibiliteMapper;
import com.example.MomyCare.model.Disponibilite;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.dao.DisponibiliteRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.service.DisponibiliteService;
import com.example.MomyCare.validation.DisponibiliteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibiliteServiceImpl implements DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final GynecologueRepository gynecologueRepository;
    private final DisponibiliteMapper disponibiliteMapper;
    private final DisponibiliteValidator disponibiliteValidator;

    @Override
    public List<DisponibiliteDTO> getDisponibilitesParGyneco(Long gynecologueId) {
        return disponibiliteMapper.toDtoList(disponibiliteRepository.findByGynecologueId(gynecologueId));
    }

    @Override
    public List<DisponibiliteDTO> getMesDisponibilites(Long gynecologueId) {
        return getDisponibilitesParGyneco(gynecologueId);
    }

    @Override
    public DisponibiliteDTO getDisponibiliteById(Long id) {
        return disponibiliteMapper.toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public DisponibiliteDTO creerDisponibilite(Long gynecologueId, DisponibiliteRequestDTO dto) {
        log.info("Création d'une disponibilité pour gynécologue {}", gynecologueId);

        disponibiliteValidator.validerCoherenceHeures(dto);
        disponibiliteValidator.validerUnicite(gynecologueId, dto);
        disponibiliteValidator.validerAbsenceChevauchement(gynecologueId, dto);

        Gynecologue gynecologue = gynecologueRepository.findById(gynecologueId)
                .orElseThrow(() -> new ResourceNotFoundException("Gynécologue introuvable : " + gynecologueId));

        DayOfWeek jour = dto.getDate().getDayOfWeek();
        Disponibilite entity = Disponibilite.builder()
                .jourSemaine(jour)
                .heureDebut(dto.getHeureDebut())
                .heureFin(dto.getHeureFin())
                .gynecologue(gynecologue)
                .build();

        Disponibilite saved = disponibiliteRepository.save(entity);
        log.info("Disponibilité créée avec l'ID {}", saved.getId());
        return disponibiliteMapper.toDto(saved);
    }

    @Override
    @Transactional
    public DisponibiliteDTO mettreAJourDisponibilite(Long id, Long gynecologueId, DisponibiliteRequestDTO dto) {
        Disponibilite existante = findOrThrow(id);
        if (!existante.getGynecologue().getId().equals(gynecologueId))
            throw new ForbiddenException("Cette disponibilité ne vous appartient pas.");

        disponibiliteValidator.validerCoherenceHeures(dto);
        disponibiliteValidator.validerUnicitePourUpdate(gynecologueId, id, dto);
        disponibiliteValidator.validerAbsenceChevauchementPourUpdate(gynecologueId, id, dto);

        if (dto.getDate() != null) existante.setJourSemaine(dto.getDate().getDayOfWeek());
        existante.setHeureDebut(dto.getHeureDebut());
        existante.setHeureFin(dto.getHeureFin());

        Disponibilite updated = disponibiliteRepository.save(existante);
        return disponibiliteMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteDisponibilite(Long id, Long gynecologueId) {
        Disponibilite existante = findOrThrow(id);
        if (!existante.getGynecologue().getId().equals(gynecologueId))
            throw new AccessDeniedException("Cette disponibilité ne vous appartient pas.");
        disponibiliteRepository.deleteById(id);
        log.info("Disponibilité {} supprimée", id);
    }

    private Disponibilite findOrThrow(Long id) {
        return disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée : " + id));
    }
}