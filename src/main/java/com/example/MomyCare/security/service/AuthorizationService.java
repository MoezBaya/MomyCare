package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.StatutRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final GynecologueRepository gynecologueRepo;
    private final PatienteRepository patienteRepo;
    private final RelationRepository relationRepo;
    private final ConsultationRepository consultationRepo;

    // ================= CURRENT USER =================

    public Gynecologue getCurrentGyneco(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        return gynecologueRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Gynécologue non trouvé"
                ));
    }

    public Patiente getCurrentPatiente(Authentication auth) {
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        return patienteRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patiente non trouvée"
                ));
    }

    // ================= RELATION CHECK =================

    public void checkRelationActive(Long patienteId, Long gynecoId) {

        boolean exists = relationRepo
                .existsByPatiente_IdAndGynecologue_IdAndStatus(
                        patienteId,
                        gynecoId,
                        StatutRelation.ACTIVE
                );

        if (!exists) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Aucune relation active avec cette patiente"
            );
        }
    }

    // ================= CONSULTATION ACCESS =================

    public Consultation getConsultationIfAuthorized(
            Long consultationId,
            Long gynecoId
    ) {

        Consultation consultation = consultationRepo.findById(consultationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Consultation non trouvée"
                ));

        if (!consultation.getGynecologue().getId().equals(gynecoId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Accès refusé à cette consultation"
            );
        }

        return consultation;
    }

    // ================= PATIENT ACCESS =================

    public void checkAccessToPatient(Long patienteId, Long gynecoId) {
        checkRelationActive(patienteId, gynecoId);
    }
}
