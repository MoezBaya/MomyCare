package com.example.MomyCare.service;

import com.example.MomyCare.dto.Imagerie.ImagerieRequestDTO;
import com.example.MomyCare.dto.Imagerie.ImagerieResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagerieService {
    List<ImagerieResponseDTO> getByConsultation(Long id);

    ImagerieResponseDTO addImagerie(Long id, ImagerieRequestDTO dto, MultipartFile file);
}
