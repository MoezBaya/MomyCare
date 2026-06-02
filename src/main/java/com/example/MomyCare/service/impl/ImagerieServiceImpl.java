package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.ImagerieRepository;
import com.example.MomyCare.dto.Imagerie.ImagerieRequestDTO;
import com.example.MomyCare.dto.Imagerie.ImagerieResponseDTO;
import com.example.MomyCare.mapper.ImagerieMapper;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Imagerie;
import com.example.MomyCare.security.service.SecurityContextService;
import com.example.MomyCare.service.ImagerieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImagerieServiceImpl implements ImagerieService {

    private final ImagerieRepository   imagerieRepo;
    private final ImagerieMapper       mapper;
    private final FileStorageService fileStorageService;
    private final SecurityContextService security;

    // ─── POST : only gynaecologist can add ───────────────────────────────────
    public ImagerieResponseDTO addImagerie(
            Long consultationId,
            ImagerieRequestDTO dto,
            MultipartFile file
    ) {
        Gynecologue gyneco = security.getGyneco();  // throws if not a gynaecologist
        Consultation consultation = security.getConsultationIfAuthorized(consultationId, gyneco.getId());

        String path = fileStorageService.saveFile(file);

        Imagerie img = mapper.toEntity(dto);
        img.setConsultation(consultation);
        img.setDateImagerie(LocalDate.now());
        img.setFichierNom(file.getOriginalFilename());
        img.setFichierPath(path);

        return mapper.toDto(imagerieRepo.save(img));
    }

    // ─── GET : both gynaecologist and patient can view (own data) ────────────
    @Override
    @Transactional(readOnly = true)
    public List<ImagerieResponseDTO> getByConsultation(Long consultationId) {
        security.authorizeConsultationAccess(consultationId);
        return mapper.toDtoList(imagerieRepo.findByConsultation_IdConsultation(consultationId));
    }
}