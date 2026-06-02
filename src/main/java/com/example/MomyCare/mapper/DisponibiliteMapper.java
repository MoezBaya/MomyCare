package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.model.Disponibilite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DisponibiliteMapper {

    // Requête → Entité (on ignore les champs gérés manuellement)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jourSemaine", ignore = true)   // défini dans le service
    @Mapping(target = "gynecologue", ignore = true)  // défini dans le service
    Disponibilite toEntity(DisponibiliteRequestDTO dto);

    // Entité → DTO
    DisponibiliteDTO toDto(Disponibilite entity);

    List<DisponibiliteDTO> toDtoList(List<Disponibilite> entities);

    // Mise à jour
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jourSemaine", ignore = true)
    @Mapping(target = "gynecologue", ignore = true)
    void updateEntityFromDto(DisponibiliteRequestDTO dto, @MappingTarget Disponibilite entity);
}