package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;
import com.example.MomyCare.model.Disponibilite;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DisponibiliteMapper {

    /**
     * Entity → ResponseDTO.
     * Le champ gynecologueId est extrait depuis gynecologue.id.
     */
    @Mapping(source = "gynecologue.id", target = "gynecologueId")
    DisponibiliteDTO toDto(Disponibilite disponibilite);

    /**
     * RequestDTO → Entity (sans gynecologue — à setter manuellement dans le service).
     * Le champ id est ignoré pour forcer la génération en base.
     */
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "gynecologue", ignore = true)
    Disponibilite toEntity(DisponibiliteRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité existante depuis un RequestDTO.
     * Utilisé dans le flow PUT /api/disponibilites/{id}.
     */
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "gynecologue", ignore = true)
    void updateEntityFromDto(DisponibiliteRequestDTO dto, @MappingTarget Disponibilite entity);

    /** Mapper une liste d'entités → liste de DTOs. */
    List<DisponibiliteDTO> toDtoList(List<Disponibilite> disponibilites);
}


