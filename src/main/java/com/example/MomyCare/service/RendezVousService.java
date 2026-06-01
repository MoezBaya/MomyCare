package com.example.MomyCare.service;

import com.example.MomyCare.dao.DossierMedicaleRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dao.RendezVousRepository;
import com.example.MomyCare.dto.rdv.RendezVousRequestDTO;
import com.example.MomyCare.dto.rdv.RendezVousResponseDTO;
import com.example.MomyCare.exception.CreneauIndisponibleException;
import com.example.MomyCare.mapper.RendezVousMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.validation.DisponibiliteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final RendezVousMapper rendezVousMapper;
    private final RelationRepository relationRepository;
    private final CreneauService creneauService;
    private final DisponibiliteValidator validator;
    private final SecurityContextService security;
    private final GynecologueRepository gynecologueRepository; // ← AJOUTER CETTE LIGNE
    private final DossierMedicaleRepository dossierMedicaleRepository;

    @Transactional
    public RendezVousResponseDTO demanderRdv(RendezVousRequestDTO dto) {

        Patiente patiente = security.getPatiente();

        validator.validerDateFuture(dto.getDateRendezVous());

        if (!creneauService.isCreneauLibre(dto.getGynecologueId(), dto.getDateRendezVous())) {
            throw new RuntimeException("Créneau indisponible");
        }

        // ⭐ CHARGER LE GYNÉCOLOGUE À PARTIR DE L'ID REÇU
        Gynecologue gynecologue = gynecologueRepository.findById(dto.getGynecologueId())
                .orElseThrow(() -> new RuntimeException("Gynécologue inexistant"));

        RendezVous rdv = rendezVousMapper.toEntity(dto);
        rdv.setGynecologue(gynecologue);   // ← LIAISON ESSENTIELLE
        rdv.setPatiente(patiente);
        rdv.setStatusRDV(StatusRDV.EN_ATTENTE);

        return rendezVousMapper.toDto(rendezVousRepository.save(rdv));
    }

    // ================= EN ATTENTE GYNECO =================

    public List<RendezVousResponseDTO> getMesRdvEnAttente() {
        Long gynecoId = security.getGyneco().getId();

        return rendezVousRepository.findByGynecologue_IdAndStatusRDV(
                        gynecoId, StatusRDV.EN_ATTENTE)
                .stream()
                .map(rendezVousMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getGynecoRdv() {
        Long gynecoId = security.getGyneco().getId();
        return rendezVousRepository.findByGynecologue_Id(gynecoId)
                .stream()
                .map(rendezVousMapper::toDto)
                .toList();
    }

    // ================= REPONDRE =================

    // ─── Gynéco : accepter ou refuser ────────────────────────────────────────
    @Transactional
    public RendezVousResponseDTO repondreRdv(Long rdvId, boolean accepter) {
        Gynecologue gyneco = security.getGyneco();
        if (gyneco == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gynécologue non authentifié");
        }
        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RDV non trouvé"));
        if (!rdv.getGynecologue().getId().equals(gyneco.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ce RDV ne vous appartient pas");
        }
        if (rdv.getStatusRDV() != StatusRDV.EN_ATTENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce RDV a déjà été traité");
        }
        if (accepter) {
            rdv.setStatusRDV(StatusRDV.CONFIRME);
            creerRelationEtDossier(rdv.getPatiente(), gyneco);
        } else {
            rdv.setStatusRDV(StatusRDV.REFUSER);
        }
        return rendezVousMapper.toDto(rendezVousRepository.save(rdv));
    }


    // ================= MES RDV =================

    public List<RendezVousResponseDTO> getMesRdv() {
        Long patienteId = security.getPatiente().getId();

        return rendezVousRepository.findByPatiente_Id(patienteId)
                .stream()
                .map(rendezVousMapper::toDto)
                .toList();
    }

    // ─── Logique interne : Relation + Dossier médical ────────────────────────
    private void creerRelationEtDossier(Patiente patiente, Gynecologue gyneco) {
        boolean relationExiste = relationRepository.existsByPatiente_IdAndGynecologue_IdAndStatus(
                patiente.getId(), gyneco.getId(), StatutRelation.ACTIVE);

        if (!relationExiste) {
            try {
                relationRepository.save(Relation.builder()
                        .patiente(patiente)
                        .gynecologue(gyneco)
                        .status(StatutRelation.ACTIVE)
                        .dateDebut(LocalDate.now())
                        .build());
            } catch (DataIntegrityViolationException ignored) {
                log.warn("Relation déjà existante pour patiente {} / gynéco {}",
                        patiente.getId(), gyneco.getId());
            }
        }

        if (patiente.getDossierMedicale() == null) {
            try {
                dossierMedicaleRepository.save(DossierMedicale.builder()
                        .patiente(patiente)
                        .derniereModificationPar(gyneco)
                        .build());
            } catch (DataIntegrityViolationException ignored) {
                log.warn("Dossier déjà existant pour patiente {}", patiente.getId());
            }
        }
    }
}