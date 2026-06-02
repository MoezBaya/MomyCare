package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.exception.ForbiddenException;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityContextService {

    private final GynecologueRepository gynecoRepo;
    private final PatienteRepository patienteRepo;
    private final ConsultationRepository consultationRepo;

    // ==================== MÉTHODES PUBLIQUES ====================

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

    public boolean isCurrentGynecologue(Long gynecoId) {
        return getGyneco().getId().equals(gynecoId);
    }

    public boolean isCurrentPatiente(Long patienteId) {
        return getPatiente().getId().equals(patienteId);
    }

    // ==================== MÉTHODE PRIVÉE ====================

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return user.getId();
    }

    // ─── Accès consultation pour gynécologue uniquement (POST, PUT, DELETE) ───
    public Consultation getConsultationIfAuthorized(Long consultationId, Long gynecoId) {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée"));

        if (!consultation.getGynecologue().getId().equals(gynecoId)) {
            throw new ForbiddenException("Accès refusé à cette consultation");
        }
        return consultation;
    }

    // ─── Accès consultation pour gynécologue OU patiente (GET) ───────────────
    public void authorizeConsultationAccess(Long consultationId) {
        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isGyneco = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GYNECOLOGUE"));
        boolean isPatiente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENTE"));

        if (isGyneco) {
            Gynecologue gyneco = getGyneco();
            if (!consultation.getGynecologue().getId().equals(gyneco.getId())) {
                throw new ForbiddenException("Vous n'avez pas accès à cette consultation");
            }
        } else if (isPatiente) {
            Patiente patiente = getPatiente();
            if (!consultation.getDossierMedicale().getPatiente().getId().equals(patiente.getId())) {
                throw new ForbiddenException("Cette consultation ne vous appartient pas");
            }
        } else {
            throw new ForbiddenException("Rôle non supporté");
        }
    }
}