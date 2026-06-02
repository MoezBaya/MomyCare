package com.example.MomyCare.service;

import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import org.springframework.security.core.Authentication;

public interface DossierMedicalService {
    DossierMedicaleResponseDTO createForPatiente(Long patienteId, CreateDossierMedicaleDTO dto);

    DossierMedicaleResponseDTO getByPatienteId(Authentication auth, Long patienteId);

    DossierMedicaleResponseDTO update(Long patienteId, CreateDossierMedicaleDTO dto);

    DossierMedicaleResponseDTO getMyDossier(Authentication auth);
}
