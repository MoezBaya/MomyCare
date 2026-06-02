package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.MedicamentRepository;
import com.example.MomyCare.dto.medicament.MedicamentRequestDTO;
import com.example.MomyCare.dto.medicament.MedicamentResponseDTO;
import com.example.MomyCare.exception.ResourceNotFoundException;
import com.example.MomyCare.mapper.MedicamentMapper;
import com.example.MomyCare.model.Medicament;
import com.example.MomyCare.service.MedicamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository medicamentRepository;
    private final MedicamentMapper medicamentMapper;

    @Transactional(readOnly = true)
    public List<MedicamentResponseDTO> getAllMedicaments() {
        return medicamentMapper.toResponseDTOList(medicamentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public MedicamentResponseDTO getMedicamentById(Long id) {
        return medicamentMapper.toResponseDTO(findOrThrow(id));
    }

    @Transactional
    public MedicamentResponseDTO createMedicament(MedicamentRequestDTO dto) {
        Medicament saved = medicamentRepository.save(medicamentMapper.toEntity(dto));
        log.info("Médicament créé : {}", saved.getCodeMedicament());
        return medicamentMapper.toResponseDTO(saved);
    }

    @Transactional
    public MedicamentResponseDTO updateMedicament(Long id, MedicamentRequestDTO dto) {
        Medicament medicament = findOrThrow(id);
        medicamentMapper.updateEntityFromDto(dto, medicament);
        Medicament updated = medicamentRepository.save(medicament);
        log.info("Médicament {} mis à jour", id);
        return medicamentMapper.toResponseDTO(updated);
    }

    @Transactional
    public void deleteMedicament(Long id) {
        Medicament medicament = findOrThrow(id);
        medicamentRepository.delete(medicament);
        log.info("Médicament {} supprimé", id);
    }

    public Medicament findOrThrow(Long id) {
        return medicamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médicament introuvable avec l'id : " + id));
    }
}