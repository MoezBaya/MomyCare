package com.example.MomyCare.service;

import com.example.MomyCare.dto.medicament.MedicamentRequestDTO;
import com.example.MomyCare.dto.medicament.MedicamentResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface MedicamentService {
    List<MedicamentResponseDTO> getAllMedicaments();

    MedicamentResponseDTO getMedicamentById(Long id);

    MedicamentResponseDTO createMedicament(@Valid MedicamentRequestDTO dto);

    MedicamentResponseDTO updateMedicament(Long id, @Valid MedicamentRequestDTO dto);

    void deleteMedicament(Long id);
}
