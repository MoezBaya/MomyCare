package com.example.MomyCare.service;

import com.example.MomyCare.dao.DossierMedicaleRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.dao.RendezVousRepository;
import com.example.MomyCare.dto.rdv.RendezVousRequestDTO;
import com.example.MomyCare.dto.rdv.RendezVousResponseDTO;
import com.example.MomyCare.mapper.RendezVousMapper;
import com.example.MomyCare.model.DossierMedicale;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.Relation;
import com.example.MomyCare.model.RendezVous;
import com.example.MomyCare.model.StatutRelation;
import com.example.MomyCare.model.StatusRDV;
import com.example.MomyCare.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService {

    private final RendezVousRepository   rdvRepo;
    private final PatienteRepository     patienteRepo;
    private final GynecologueRepository  gynecologueRepo;
    private final RelationRepository     relationRepo;
    private final DossierMedicaleRepository dossierRepo;
    private final RendezVousMapper       mapper;
    private final SecurityContextService security;

    // ─── Patiente : demander un RDV ───────────────────────────────────────────

    public RendezVousResponseDTO demanderRdv(Authentication auth, RendezVousRequestDTO dto) {
        Patiente    patiente = security.getPatiente(auth);
        Gynecologue gyneco   = gynecologueRepo.findById(dto.getGynecologueId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));

        if (rdvRepo.existsByPatienteAndGynecologueAndStatusRDV(patiente, gyneco, StatusRDV.EN_ATTENTE)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Vous avez déjà un RDV en attente avec ce gynécologue");
        }

        RendezVous rdv = RendezVous.builder()
                .patiente(patiente)
                .gynecologue(gyneco)
                .dateRendezVous(dto.getDateRendezVous())
                .motif(dto.getMotif())
                .statusRDV(StatusRDV.EN_ATTENTE)
                .build();

        return mapper.toDto(rdvRepo.save(rdv));
    }

    // ─── Gynéco : accepter ou refuser ────────────────────────────────────────

    public RendezVousResponseDTO repondreRdv(Authentication auth, Long rdvId, boolean accepter) {
        Gynecologue gyneco = security.getGyneco(auth);

        RendezVous rdv = rdvRepo.findById(rdvId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "RDV non trouvé"));

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

        return mapper.toDto(rdvRepo.save(rdv));
    }

    // ─── Gynéco : voir ses RDV en attente ────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getMesRdvEnAttente(Authentication auth) {
        Gynecologue gyneco = security.getGyneco(auth);
        return mapper.toDtoList(
                rdvRepo.findByGynecologueAndStatusRDV(gyneco, StatusRDV.EN_ATTENTE));
    }

    // ─── Patiente : voir ses RDV ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getMesRdv(Authentication auth) {
        Patiente patiente = security.getPatiente(auth);
        return mapper.toDtoList(rdvRepo.findByPatiente(patiente));
    }

    // ─── Logique interne : Relation + Dossier médical ────────────────────────

    private void creerRelationEtDossier(Patiente patiente, Gynecologue gyneco) {

        boolean relationExiste = relationRepo.existsByPatiente_IdAndGynecologue_IdAndStatus(
                patiente.getId(), gyneco.getId(), StatutRelation.ACTIVE);

        if (!relationExiste) {
            try {
                relationRepo.save(Relation.builder()
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
                dossierRepo.save(DossierMedicale.builder()
                        .patiente(patiente)
                        .derniereModificationPar(gyneco)
                        .build());
            } catch (DataIntegrityViolationException ignored) {
                log.warn("Dossier déjà existant pour patiente {}", patiente.getId());
            }
        }
    }
}