package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.OrdonnanceRepository;
import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import com.example.MomyCare.exception.BadRequestException;
import com.example.MomyCare.exception.ConflictException;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.OrdonnanceMapper;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.LigneOrdonnance;
import com.example.MomyCare.model.Ordonnance;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.OrdonnanceService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository   ordonnanceRepo;
    private final OrdonnanceMapper       mapper;
    private final SecurityContextService security;

    @Override
    public OrdonnanceResponseDTO createOrdonnance(Long consultationId, OrdonnanceRequestDTO dto) {
        Gynecologue  gyneco       = security.getGyneco();
        Consultation consultation = security.getConsultationIfAuthorized(consultationId, gyneco.getId());

        if (ordonnanceRepo.existsByNumOrdonnanceAndConsultation_IdConsultation(
                dto.getNumOrdonnance(), consultationId)) {
            throw new ConflictException("Ce numéro d'ordonnance existe déjà");
        }

        Ordonnance ordonnance = mapper.toEntity(dto);
        ordonnance.setConsultation(consultation);

        List<LigneOrdonnance> lignes = dto.getLignes()
                .stream()
                .map(mapper::toLigneEntity)
                .toList();

        lignes.forEach(l -> l.setOrdonnance(ordonnance));
        ordonnance.setLigneOrdonnances(lignes);

        return mapper.toResponseDTO(ordonnanceRepo.save(ordonnance));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdonnanceResponseDTO> getOrdonnancesByConsultation(Long consultationId) {
        security.authorizeConsultationAccess(consultationId);
        return mapper.toResponseDTOList(
                ordonnanceRepo.findByConsultation_IdConsultation(consultationId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrdonnanceResponseDTO getOrdonnanceById(Long consultationId, Long ordonnanceId) {
        security.authorizeConsultationAccess(consultationId);
        Ordonnance ordonnance = findOrThrow(ordonnanceId);
        validateBelongsToConsultation(ordonnance, consultationId);
        return mapper.toResponseDTO(ordonnance);
    }

    @Override
    public void deleteOrdonnance(Long consultationId, Long ordonnanceId) {
        Gynecologue gyneco = security.getGyneco();
        security.getConsultationIfAuthorized(consultationId, gyneco.getId());

        Ordonnance ordonnance = findOrThrow(ordonnanceId);
        validateBelongsToConsultation(ordonnance, consultationId);

        ordonnanceRepo.delete(ordonnance);
    }

    public Ordonnance findOrThrow(Long ordonnanceId) {
        return ordonnanceRepo.findById(ordonnanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance non trouvée"));
    }

    private void validateBelongsToConsultation(Ordonnance ordonnance, Long consultationId) {
        if (!ordonnance.getConsultation().getIdConsultation().equals(consultationId)) {
            throw new BadRequestException("Cette ordonnance n'appartient pas à cette consultation");
        }
    }


}