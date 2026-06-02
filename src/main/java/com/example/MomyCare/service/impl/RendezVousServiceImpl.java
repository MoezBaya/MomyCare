package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.*;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.rdv.RendezVousRequestDTO;
import com.example.MomyCare.dto.rdv.RendezVousResponseDTO;
import com.example.MomyCare.exception.BadRequestException;
import com.example.MomyCare.exception.CreneauIndisponibleException;
import com.example.MomyCare.exception.ForbiddenException;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.PatienteMapper;
import com.example.MomyCare.mapper.RendezVousMapper;
import com.example.MomyCare.model.*;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.AuthService;
import com.example.MomyCare.service.CreneauService;
import com.example.MomyCare.service.RendezVousService;
import com.example.MomyCare.validation.DisponibiliteValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final RendezVousMapper rendezVousMapper;
    private final RelationRepository relationRepository;
    private final CreneauService creneauService;
    private final DisponibiliteValidator disponibiliteValidator;
    private final SecurityContextService security;
    private final GynecologueRepository gynecologueRepository;
    private final DossierMedicaleRepository dossierMedicaleRepository;
    private final AuthService authService;
    private final PatienteMapper patienteMapper;
    private final PatienteRepository patienteRepository;
    private final DisponibiliteRepository disponibiliteRepository; // ← ajout pour supprimer la dispo

    // ============================================================
    // MÉTHODE PRIVÉE : SUPPRESSION DE LA DISPONIBILITÉ APRÈS RÉSERVATION
    // ============================================================

    /**
     * Supprime la disponibilité qui couvre le créneau réservé.
     */
    private void supprimerDisponibilitePourCreneau(Long gynecologueId, LocalDateTime dateRdv) {
        DayOfWeek jour = dateRdv.getDayOfWeek();
        LocalTime heure = dateRdv.toLocalTime();
        disponibiliteRepository.findCoveringSlot(gynecologueId, jour, heure)
                .ifPresent(dispo -> {
                    disponibiliteRepository.delete(dispo);
                    log.info("Disponibilité supprimée pour le créneau {} à {}", jour, heure);
                });
    }

    // ============================================================
    // CRÉATION DE RDV PAR LA PATIENTE
    // ============================================================

    @Transactional
    public RendezVousResponseDTO demanderRdv(RendezVousRequestDTO dto) {
        Patiente patiente = security.getPatiente();
        disponibiliteValidator.validerDateFuture(dto.getDateRendezVous()); // ← méthode ajoutée

        if (!creneauService.isCreneauLibre(dto.getGynecologueId(), dto.getDateRendezVous())) {
            throw new CreneauIndisponibleException("Créneau indisponible");
        }

        Gynecologue gynecologue = gynecologueRepository.findById(dto.getGynecologueId())
                .orElseThrow(() -> new ResourceNotFoundException("Gynécologue inexistant"));

        RendezVous rdv = rendezVousMapper.toEntity(dto);
        rdv.setGynecologue(gynecologue);
        rdv.setPatiente(patiente);
        rdv.setStatusRDV(StatusRDV.EN_ATTENTE);

        RendezVous saved = rendezVousRepository.save(rdv);

        // Suppression de la disponibilité correspondante
        supprimerDisponibilitePourCreneau(dto.getGynecologueId(), dto.getDateRendezVous());

        return rendezVousMapper.toDto(saved);
    }

    // ============================================================
    // CRÉATION DE RDV PAR LE GYNÉCOLOGUE (nouvelle patiente)
    // ============================================================

    @Override
    @Transactional
    public RendezVousResponseDTO creerRdvParGyneco(
            PatienteSignupRequest patienteDto,
            RendezVousRequestDTO rdvDTO) {

        // 1. Créer la patiente
        Patiente patiente = authService.registerPatienteAndReturnEntity(patienteDto);

        // 2. Récupérer le gynécologue connecté
        Gynecologue gyneco = security.getGyneco();

        // 3. Créer relation et dossier
        creerRelationEtDossier(patiente, gyneco);

        // 4. Valider la date (future)
        disponibiliteValidator.validerDateFuture(rdvDTO.getDateRendezVous());

        // 5. Créer le RDV (pas de vérification de disponibilité car c'est le gynéco qui impose)
        RendezVous rdv = RendezVous.builder()
                .patiente(patiente)
                .gynecologue(gyneco)
                .dateRendezVous(rdvDTO.getDateRendezVous())
                .motif(rdvDTO.getMotif())
                .statusRDV(StatusRDV.CONFIRME)
                .build();
        rdv = rendezVousRepository.save(rdv);

        // 6. Supprimer la disponibilité correspondante
        supprimerDisponibilitePourCreneau(gyneco.getId(), rdvDTO.getDateRendezVous());

        return rendezVousMapper.toDto(rdv);
    }

    // ============================================================
    // CRÉATION DE RDV PAR LE GYNÉCOLOGUE (patiente existante)
    // ============================================================

    @Transactional
    @Override
    public RendezVousResponseDTO creerRdvParGynecoExistante(
            Long patienteId,
            RendezVousRequestDTO rdvDTO) {

        // 1. Récupérer la patiente
        Patiente patiente = patienteRepository.findById(patienteId)
                .orElseThrow(() -> new EntityNotFoundException("Patiente non trouvée avec l'id: " + patienteId));

        // 2. Gynécologue connecté
        Gynecologue gyneco = security.getGyneco();

        // 3. Vérifier/créer la relation
        boolean relationExiste = relationRepository
                .existsByPatiente_IdAndGynecologue_IdAndStatus(patienteId, gyneco.getId(), StatutRelation.ACTIVE);
        if (!relationExiste) {
            creerRelationEtDossier(patiente, gyneco);
        }

        // 4. Valider la date (future)
        disponibiliteValidator.validerDateFuture(rdvDTO.getDateRendezVous());

        // 5. Créer le RDV
        RendezVous rdv = RendezVous.builder()
                .patiente(patiente)
                .gynecologue(gyneco)
                .dateRendezVous(rdvDTO.getDateRendezVous())
                .motif(rdvDTO.getMotif())
                .statusRDV(StatusRDV.CONFIRME)
                .build();
        rdv = rendezVousRepository.save(rdv);

        // 6. Supprimer la disponibilité correspondante
        supprimerDisponibilitePourCreneau(gyneco.getId(), rdvDTO.getDateRendezVous());

        return rendezVousMapper.toDto(rdv);
    }

    // ============================================================
    // LECTURE – RDV EN ATTENTE
    // ============================================================

    public List<RendezVousResponseDTO> getMesRdvEnAttente() {
        Long gynecoId = security.getGyneco().getId();
        return rendezVousRepository.findByGynecologue_IdAndStatusRDV(gynecoId, StatusRDV.EN_ATTENTE)
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

    // ============================================================
    // RÉPONSE À UN RDV (confirmer/refuser)
    // ============================================================

    @Transactional
    public RendezVousResponseDTO repondreRdv(Long rdvId, boolean accepter) {
        Gynecologue gyneco = security.getGyneco();
        if (gyneco == null) {
            throw new ForbiddenException("Gynécologue non authentifié");
        }
        RendezVous rdv = rendezVousRepository.findById(rdvId)
                .orElseThrow(() -> new ResourceNotFoundException("RDV non trouvé"));
        if (!rdv.getGynecologue().getId().equals(gyneco.getId())) {
            throw new ForbiddenException("Ce RDV ne vous appartient pas");
        }
        if (rdv.getStatusRDV() != StatusRDV.EN_ATTENTE) {
            throw new BadRequestException("Ce RDV a déjà été traité");
        }
        if (accepter) {
            rdv.setStatusRDV(StatusRDV.CONFIRME);
            creerRelationEtDossier(rdv.getPatiente(), gyneco);
            // La disponibilité a déjà été supprimée lors de la demande, donc rien à faire ici.
        } else {
            rdv.setStatusRDV(StatusRDV.REFUSER);
            // Optionnel : on pourrait réinsérer la disponibilité, mais on laisse le gynéco le faire manuellement.
        }
        return rendezVousMapper.toDto(rendezVousRepository.save(rdv));
    }

    // ============================================================
    // MES RDV (PATIENTE)
    // ============================================================

    public List<RendezVousResponseDTO> getMesRdv() {
        Long patienteId = security.getPatiente().getId();
        return rendezVousRepository.findByPatiente_Id(patienteId)
                .stream()
                .map(rendezVousMapper::toDto)
                .toList();
    }

    // ============================================================
    // MÉTHODES PRIVÉES – RELATION / DOSSIER
    // ============================================================

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
