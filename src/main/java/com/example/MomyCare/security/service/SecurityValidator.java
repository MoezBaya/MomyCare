package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.StatutRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class SecurityValidator {

    private final RelationRepository relationRepo;

    public void checkActiveRelation(Long patienteId, Long gynecoId) {
        relationRepo.findByPatiente_IdAndGynecologue_IdAndStatus(
                patienteId, gynecoId, StatutRelation.ACTIVE
        ).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.FORBIDDEN, "Relation inactive"));
    }

    public void checkConsultationOwnership(Consultation c, Gynecologue g) {
        if (!c.getGynecologue().getId().equals(g.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your consultation");
        }
    }
}
