package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class SecurityContextService {

    private final GynecologueRepository gynecoRepo;
    private final PatienteRepository patienteRepo;
    private final ConsultationRepository consultationRepo;



    // ==================== MÉTHODES PUBLIQUES ====================

    /**
     * Récupère le Gynecologue connecté - SEULE méthode autorisée
     */
    public Gynecologue getGyneco() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        boolean isGyneco = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GYNECOLOGUE"));
        if (!isGyneco) {
            throw new AccessDeniedException("Not a gynecologist");
        }
        return gynecoRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Gynecologue non trouvé"));
    }

    /**
     * Récupère la Patiente connectée - SEULE méthode autorisée
     */
    public Patiente getPatiente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        boolean isPatient = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENTE"));

        if (!isPatient) {
            throw new AccessDeniedException("Not a patient");
        }

        return patienteRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Patiente non trouvée"));
    }

    /**
     * Vérifie si l'utilisateur connecté EST ce gynécologue
     */
    public boolean isCurrentGynecologue(Long gynecoId) {
        return getGyneco().getId().equals(gynecoId);
    }

    /**
     * Vérifie si l'utilisateur connecté EST cette patiente
     */
    public boolean isCurrentPatiente(Long patienteId) {
        return getPatiente().getId().equals(patienteId);
    }

    // ==================== MÉTHODE PRIVÉE ====================

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return user.getId();  // Toujours le User.id !
    }


    // ─── Accès consultation ───────────────────────────────────────────────────

    public Consultation getConsultationIfAuthorized(Long consultationId, Long gynecoId) {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Consultation non trouvée"));

        if (!consultation.getGynecologue().getId().equals(gynecoId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Accès refusé à cette consultation");
        }
        return consultation;

    }
}