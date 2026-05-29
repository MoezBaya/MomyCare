package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class SecurityContextService {

    private final GynecologueRepository gynecoRepo;
    private final PatienteRepository    patienteRepo;
    private final ConsultationRepository consultationRepo;

    // ─── Résolution de l'identité ────────────────────────────────────────────

    public Gynecologue getGyneco(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return gynecoRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Gynécologue non trouvé"));
    }

    public Patiente getPatiente(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return patienteRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente non trouvée"));
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