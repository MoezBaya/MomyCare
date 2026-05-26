package com.example.MomyCare.service;

import com.example.MomyCare.dao.DossierMedicaleRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import com.example.MomyCare.mapper.DossierMedicalMapper;
import com.example.MomyCare.model.DossierMedicale;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.security.service.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DossierMedicalService {

    private final DossierMedicaleRepository dossierRepo;
    private final PatienteRepository patienteRepo;
    private final DossierMedicalMapper mapper;

    // CREATE
    @Transactional
    public DossierMedicaleResponseDTO createForPatiente(Long patienteId, CreateDossierMedicaleDTO dto) {

        Patiente patiente = patienteRepo.findById(patienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patiente not found"
                ));

        if (patiente.getDossierMedicale() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Dossier already exists for this patiente"
            );
        }

        DossierMedicale dossier = mapper.toEntity(dto);
        dossier.setPatiente(patiente);

        return mapper.toDto(dossierRepo.save(dossier));
    }

    // GET ALL
    public List<DossierMedicaleResponseDTO> getAll() {
        return mapper.toDtoList(dossierRepo.findAll());
    }


    public DossierMedicaleResponseDTO getByPatientId(Long patientId) {
        Patiente p = patienteRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patiente not found"));

        if (p.getDossierMedicale() == null) {
            throw new RuntimeException("Dossier not found");
        }

        return mapper.toDto(p.getDossierMedicale());
    }

    // UPDATE
    public DossierMedicaleResponseDTO update(Long id, CreateDossierMedicaleDTO dto) {

        DossierMedicale dossier = dossierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Dossier not found"));

        mapper.updateEntityFromDto(dto, dossier);

        return mapper.toDto(dossierRepo.save(dossier));
    }

    // MY DOSSIER
    public DossierMedicaleResponseDTO getMyDossier(Authentication auth) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Patiente patiente = patienteRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patiente not found"));

        return mapper.toDto(patiente.getDossierMedicale());
    }
}