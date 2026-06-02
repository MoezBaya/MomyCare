package com.example.MomyCare.service;

import com.example.MomyCare.dto.Analyse.AnalyseRequestDTO;
import com.example.MomyCare.dto.Analyse.AnalyseResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnalyseService {
    AnalyseResponseDTO addAnalyse(Long id, AnalyseRequestDTO dto, MultipartFile file);

    List<AnalyseResponseDTO> getByConsultation( Long id);
}
