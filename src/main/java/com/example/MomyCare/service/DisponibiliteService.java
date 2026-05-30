package com.example.MomyCare.service;


import com.example.MomyCare.dao.DisponibiliteRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.mapper.DisponibiliteMapper;
import com.example.MomyCare.model.Disponibilite;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepo;
    private final GynecologueRepository gynecologueRepo;
    private final DisponibiliteMapper mapper;
    private final SecurityContextService securityContext;

    @Transactional(readOnly = true)
    public List<DisponibiliteDTO> getDisponibilitesGyneco(Long gynecologueId) {
        Gynecologue gyneco = gynecologueRepo.findById(gynecologueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));

        return mapper.toDtoList(disponibiliteRepo.findByGynecologueAndDisponibleTrueAndDateTimeAfterOrderByDateTimeAsc(gyneco, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteDTO> getMesDisponibilites(Authentication auth) {
        Gynecologue gyneco = securityContext.getGyneco();
        return mapper.toDtoList(disponibiliteRepo.findByGynecologueOrderByDateTimeAsc(gyneco));
    }

    public List<DisponibiliteDTO> ajouterDisponibilites(Authentication auth, List<DisponibiliteRequestDTO> dtos) {
        Gynecologue gyneco = securityContext.getGyneco();
        List<Disponibilite> nouvellesDispos = new ArrayList<>();

        for (DisponibiliteRequestDTO dto : dtos) {
            // Check if slot already exists for this doctor
            if (!disponibiliteRepo.existsByGynecologueAndDateTime(gyneco, dto.getDateTime())) {
                Disponibilite dispo = Disponibilite.builder()
                        .gynecologue(gyneco)
                        .dateTime(dto.getDateTime())
                        .disponible(true)
                        .build();
                nouvellesDispos.add(dispo);
            }
        }

        return mapper.toDtoList(disponibiliteRepo.saveAll(nouvellesDispos));
    }

    public void supprimerDisponibilite(Authentication auth, Long id) {
        Gynecologue gyneco = securityContext.getGyneco();
        Disponibilite dispo = disponibiliteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disponibilité non trouvée"));

        if (!dispo.getGynecologue().getId().equals(gyneco.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette disponibilité ne vous appartient pas");
        }

        if (!dispo.isDisponible()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de supprimer un créneau déjà réservé");
        }

        disponibiliteRepo.delete(dispo);
    }
}

