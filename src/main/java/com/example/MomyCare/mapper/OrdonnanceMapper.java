package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.ordonnance.OrdonnanceRequestDTO;
import com.example.MomyCare.dto.ordonnance.OrdonnanceResponseDTO;
import com.example.MomyCare.model.Ordonnance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceResponseDTO;
import com.example.MomyCare.model.LigneOrdonnance;


@Mapper(componentModel = "spring")
public interface OrdonnanceMapper {

    // ── Ordonnance → ResponseDTO ─────────────────────────────────────

    @Mapping(target = "consultationId", source = "consultation.idConsultation")
    @Mapping(target = "lignes", source = "ligneOrdonnances")
    OrdonnanceResponseDTO toResponseDTO(Ordonnance ordonnance);

    List<OrdonnanceResponseDTO> toResponseDTOList(List<Ordonnance> ordonnances);

    // ── RequestDTO → Ordonnance (création) ───────────────────────────

    @Mapping(target = "idOrdonnance",   ignore = true)
    @Mapping(target = "consultation",   ignore = true)  // géré dans le service
    @Mapping(target = "ligneOrdonnances", ignore = true) // géré dans le service
    @Mapping(target = "createdAt",      ignore = true)  // @CreationTimestamp
    @Mapping(target = "updatedAt",      ignore = true)  // @UpdateTimestamp
    Ordonnance toEntity(OrdonnanceRequestDTO dto);

    // ── Update (PATCH) ───────────────────────────────────────────────

    @Mapping(target = "idOrdonnance",   ignore = true)
    @Mapping(target = "consultation",   ignore = true)
    @Mapping(target = "ligneOrdonnances", ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    void updateEntityFromDto(OrdonnanceRequestDTO dto, @MappingTarget Ordonnance ordonnance);

    // ── LigneOrdonnance → ResponseDTO ───────────────────────────────

    @Mapping(target = "medicamentId",   source = "medicament.codeMedicament")
    @Mapping(target = "nomMedicament",  source = "medicament.nomMedicament")
    LigneOrdonnanceResponseDTO toLigneResponseDTO(LigneOrdonnance ligne);

    List<LigneOrdonnanceResponseDTO> toLigneResponseDTOList(List<LigneOrdonnance> lignes);

    // ── RequestDTO → LigneOrdonnance ─────────────────────────────────

    @Mapping(target = "idLigneOrdonnance", ignore = true)
    @Mapping(target = "medicament",        ignore = true) // géré dans le service
    @Mapping(target = "ordonnance",        ignore = true) // géré dans le service
    @Mapping(target = "createdAt",         ignore = true)
    @Mapping(target = "updatedAt",         ignore = true)
    LigneOrdonnance toLigneEntity(LigneOrdonnanceRequestDTO dto);

    // ajouter dans OrdonnanceMapper.java

    @Mapping(target = "idLigneOrdonnance", ignore = true)
    @Mapping(target = "medicament",        ignore = true)
    @Mapping(target = "ordonnance",        ignore = true)
    @Mapping(target = "createdAt",         ignore = true)
    @Mapping(target = "updatedAt",         ignore = true)
    void updateLigneFromDto(LigneOrdonnanceRequestDTO dto, @MappingTarget LigneOrdonnance ligne);
}