package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import com.example.MomyCare.model.DossierMedicale;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel="spring")
public interface DossierMedicalMapper {

    @Mapping(target = "patienteId", source = "patiente.id")
    DossierMedicaleResponseDTO toDto(DossierMedicale dossierMedicale);

    List<DossierMedicaleResponseDTO> toDtoList(List<DossierMedicale> dossierMedicales);

    @Mapping(target = "patiente", ignore = true)
    DossierMedicale toEntity(CreateDossierMedicaleDTO creationDossierMedical);

    void updateEntityFromDto(CreateDossierMedicaleDTO dto , @MappingTarget DossierMedicale dossierMedicale);
}