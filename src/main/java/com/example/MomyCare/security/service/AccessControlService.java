package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.model.StatutRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Vérifie que la relation patiente ↔ gynécologue est active
 * avant tout accès aux données médicales d'une patiente.
 */
@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final RelationRepository relationRepo;

    public void checkRelationActive(Long patienteId, Long gynecoId) {
        boolean exists = relationRepo.existsByPatiente_IdAndGynecologue_IdAndStatus(
                patienteId, gynecoId, StatutRelation.ACTIVE);

        if (!exists) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Aucune relation active avec cette patiente");
        }
    }
}
