package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.medicament.MedicamentRequestDTO;
import com.example.MomyCare.dto.medicament.MedicamentResponseDTO;
import com.example.MomyCare.model.Medicament;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicamentMapper {

    @Mapping(target = "codeMedicament", ignore = true)
    @Mapping(target = "ligneOrdonnances", ignore = true)
    Medicament toEntity(MedicamentRequestDTO dto);

    MedicamentResponseDTO toResponseDTO(Medicament medicament);

    List<MedicamentResponseDTO> toResponseDTOList(List<Medicament> medicaments);

    @Mapping(target = "codeMedicament",   ignore = true)
    @Mapping(target = "ligneOrdonnances", ignore = true)
    void updateEntityFromDto(MedicamentRequestDTO dto, @MappingTarget Medicament medicament);
}