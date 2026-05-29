package com.example.MomyCare.service;

import com.example.MomyCare.dao.AnalyseRepository;
import com.example.MomyCare.dao.ConsultationRepository;
import com.example.MomyCare.dto.Analyse.AnalyseRequestDTO;
import com.example.MomyCare.dto.Analyse.AnalyseResponseDTO;
import com.example.MomyCare.mapper.AnalyseMapper;
import com.example.MomyCare.model.AnalyseLaboratoire;
import com.example.MomyCare.model.Consultation;
import com.example.MomyCare.model.Gynecologue;
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
public class AnalyseService {

    private final AnalyseRepository analyseRepo;
    private final AnalyseMapper mapper;
    private final ConsultationRepository consultationRepo;
    private final FileStorageService fileStorageService;
    private final AuthorizationService authService;

    // ================= CREATE =================

    public AnalyseResponseDTO addAnalyse(
            Authentication auth,
            Long consultationId,
            AnalyseRequestDTO dto,
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

        AnalyseLaboratoire analyse = mapper.toEntity(dto);
        analyse.setConsultation(consultation);
        analyse.setDateAnalyse(LocalDate.now());
        analyse.setFichierNom(file.getOriginalFilename());
        analyse.setFichierType(file.getContentType());
        analyse.setFichierPath(path);

        return mapper.toDto(analyseRepo.save(analyse));
    }

    // ================= GET =================

    public List<AnalyseResponseDTO> getByConsultation(
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
                analyseRepo.findByConsultation_IdConsultation(consultationId)
        );
    }
}
