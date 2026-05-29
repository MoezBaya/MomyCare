package com.example.MomyCare.service;

import com.example.MomyCare.dao.*;
import com.example.MomyCare.dto.rdv.RendezVousRequestDTO;
import com.example.MomyCare.dto.rdv.RendezVousResponseDTO;
import com.example.MomyCare.mapper.RendezVousMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.UserDetailsImpl;
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

    private final RendezVousRepository rdvRepo;
    private final PatienteRepository patienteRepo;
    private final GynecologueRepository gynecologueRepo;
    private final RelationRepository relationRepo;
    private final DossierMedicaleRepository dossierRepo;
    private final RendezVousMapper mapper;

    // ─── Patiente : demander un RDV ───────────────────────────────────────────
    public RendezVousResponseDTO demanderRdv(Authentication auth,
                                             RendezVousRequestDTO dto) {
        Patiente patiente = getPatiente(auth);
        Gynecologue gyneco = gynecologueRepo.findById(dto.getGynecologueId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));

        // Vérifier qu'il n'y a pas déjà un RDV EN_ATTENTE avec ce gynéco
        boolean dejaEnAttente = rdvRepo.existsByPatienteAndGynecologueAndStatusRDV(
                patiente, gyneco, StatusRDV.EN_ATTENTE);
        if (dejaEnAttente) {
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


    // ─── Gynéco : accepter ou refuser le RDV ──────────────────────────────────
    public RendezVousResponseDTO repondreRdv(Authentication auth,
                                             Long rdvId,
                                             boolean accepter) {
        Gynecologue gyneco = getGyneco(auth);

        RendezVous rdv = rdvRepo.findById(rdvId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "RDV non trouvé"));

        // Vérifier que ce RDV appartient bien à ce gynéco
        if (!rdv.getGynecologue().getId().equals(gyneco.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Ce RDV ne vous appartient pas");
        }

        if (rdv.getStatusRDV() != StatusRDV.EN_ATTENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ce RDV a déjà été traité");
        }

        if (accepter) {
            rdv.setStatusRDV(StatusRDV.CONFIRME);
            // Créer la relation automatiquement
            creerRelationEtDossier(rdv.getPatiente(), gyneco);
        } else {
            rdv.setStatusRDV(StatusRDV.REFUSER);
        }

        return mapper.toDto(rdvRepo.save(rdv));
    }

    // ─── Gynéco : voir ses RDV en attente ─────────────────────────────────────
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getMesRdvEnAttente(Authentication auth) {
        Gynecologue gyneco = getGyneco(auth);
        return mapper.toDtoList(
                rdvRepo.findByGynecologueAndStatusRDV(gyneco, StatusRDV.EN_ATTENTE));
    }

    // ─── Patiente : voir ses RDV ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getMesRdv(Authentication auth) {
        Patiente patiente = getPatiente(auth);
        return mapper.toDtoList(rdvRepo.findByPatiente(patiente));
    }

    // ─── Logique automatique : Relation + Dossier ─────────────────────────────
    private void creerRelationEtDossier(
            Patiente patiente,
            Gynecologue gyneco
    ) {

        // ================= RELATION =================

        boolean relationExiste = relationRepo
                .existsByPatiente_IdAndGynecologue_IdAndStatus(
                        patiente.getId(),
                        gyneco.getId(),
                        StatutRelation.ACTIVE
                );

        if (!relationExiste) {

            Relation relation = Relation.builder()
                    .patiente(patiente)
                    .gynecologue(gyneco)
                    .status(StatutRelation.ACTIVE)
                    .dateDebut(LocalDate.now())
                    .build();

            try {

                relationRepo.save(relation);

            } catch (DataIntegrityViolationException ignored) {
                log.warn(
                        "Relation déjà créée pour la patiente {} et le gynécologue {}",
                        patiente.getId(),
                        gyneco.getId()
                );
            }
        }

        // ================= DOSSIER MÉDICAL =================

        if (patiente.getDossierMedicale() == null) {

            DossierMedicale dossier = DossierMedicale.builder()
                    .patiente(patiente)
                    .derniereModificationPar(gyneco)
                    .build();

            try {

                dossierRepo.save(dossier);

            } catch (DataIntegrityViolationException ignored) {
                log.warn("Dossier déjà créée pour la patiente {}", patiente.getId());
            }
        }
    }


    // ─── Helpers ──────────────────────────────────────────────────────────────
    private Patiente getPatiente(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return patienteRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));
    }

    private Gynecologue getGyneco(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return gynecologueRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));
    }
}
