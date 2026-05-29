package com.example.MomyCare.service;

import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dao.ImagerieRepository;
import com.example.MomyCare.dto.Imagerie.ImagerieRequestDTO;
import com.example.MomyCare.dto.Imagerie.ImagerieResponseDTO;
import com.example.MomyCare.mapper.ImagerieMapper;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Imagerie;
import com.example.MomyCare.security.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImagerieService {

    private final ImagerieRepository imagerieRepo;
    private final ImagerieMapper mapper;
    private final FileStorageService fileStorageService;
    private final AuthorizationService authService;

    public ImagerieResponseDTO addImagerie(
            Authentication auth,
            Long consultationId,
            ImagerieRequestDTO dto,
            MultipartFile file
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        Consultation consultation =
                authService.getConsultationIfAuthorized(
                        consultationId,
                        gyneco.getId()
                );

        String path = fileStorageService.saveFile(file);

        Imagerie img = mapper.toEntity(dto);
        img.setConsultation(consultation);
        img.setDateImagerie(LocalDate.now());
        img.setFichierNom(file.getOriginalFilename());
        img.setFichierPath(path);

        return mapper.toDto(imagerieRepo.save(img));
    }

    public List<ImagerieResponseDTO> getByConsultation(
            Authentication auth,
            Long consultationId
    ) {

        Gynecologue gyneco =
                authService.getCurrentGyneco(auth);

        authService.getConsultationIfAuthorized(
                consultationId,
                gyneco.getId()
        );

        return mapper.toDtoList(
                imagerieRepo.findByConsultation_IdConsultation(consultationId)
        );
    }
}
