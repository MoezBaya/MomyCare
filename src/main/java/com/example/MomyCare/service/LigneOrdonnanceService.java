package com.example.MomyCare.service;

import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface LigneOrdonnanceService {
    LigneOrdonnanceResponseDTO addLigne(Long ordonnanceId, LigneOrdonnanceRequestDTO ligneOrdonnance, Authentication auth);

    LigneOrdonnanceResponseDTO updateLigne(Long ordonnanceId, Long ligneOrodnnanceId, LigneOrdonnanceRequestDTO dto, Authentication auth);

    LigneOrdonnanceResponseDTO getLigneById(Long ordonnanceId, Long ligneOrdonnanceId);

    List<LigneOrdonnanceResponseDTO> getLignesByOrdonnance(Long ordonnanceId);

    void deleteLigne(Long ligneOrdonnanceId, Long ordonnanceId, Authentication auth);
}
