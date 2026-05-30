package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import com.example.MomyCare.model.LigneOrdonnance;
import com.example.MomyCare.model.Ordonnance;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdonnanceMapper {

    // ===================== RESPONSE =====================

    @Mapping(target = "consultationId", source = "consultation.idConsultation")
    @Mapping(target = "lignes", source = "ligneOrdonnances")
    OrdonnanceResponseDTO toResponseDTO(Ordonnance ordonnance);

    List<OrdonnanceResponseDTO> toResponseDTOList(List<Ordonnance> ordonnances);

    // ===================== CREATE ENTITY =====================

    @Mapping(target = "idOrdonnance", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    @Mapping(target = "ligneOrdonnances", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ordonnance toEntity(OrdonnanceRequestDTO dto);

    // ===================== UPDATE ENTITY =====================

    @Mapping(target = "idOrdonnance", ignore = true)
    @Mapping(target = "consultation", ignore = true)
    @Mapping(target = "ligneOrdonnances", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Ordonnance ordonnance, OrdonnanceRequestDTO dto);

    // ===================== LIGNE RESPONSE =====================

    @Mapping(target = "medicamentId", source = "medicament.codeMedicament")
    @Mapping(target = "nomMedicament", source = "medicament.nomMedicament")
    LigneOrdonnanceResponseDTO toLigneResponseDTO(LigneOrdonnance ligne);

    List<LigneOrdonnanceResponseDTO> toLigneResponseDTOList(List<LigneOrdonnance> lignes);

    // ===================== LIGNE ENTITY =====================

    @Mapping(target = "idLigneOrdonnance", ignore = true)
    @Mapping(target = "medicament", ignore = true)
    @Mapping(target = "ordonnance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LigneOrdonnance toLigneEntity(LigneOrdonnanceRequestDTO dto);

    @Mapping(target = "idLigneOrdonnance", ignore = true)
    @Mapping(target = "medicament", ignore = true)
    @Mapping(target = "ordonnance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateLigneFromDto( LigneOrdonnanceRequestDTO dto , @MappingTarget LigneOrdonnance ligne);
}