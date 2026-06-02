package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.RelationRepository;
import com.example.MomyCare.exception.ForbiddenException;
import com.example.MomyCare.model.StatutRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final RelationRepository relationRepo;

    public void checkRelationActive(Long patienteId, Long gynecoId) {
        boolean exists = relationRepo.existsByPatiente_IdAndGynecologue_IdAndStatus(
                patienteId, gynecoId, StatutRelation.ACTIVE);

        if (!exists) {
            throw new ForbiddenException("Aucune relation active avec cette patiente");
        }
    }
}