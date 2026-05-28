package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.rdv.RendezVousRequestDTO;
import com.example.MomyCare.dto.rdv.RendezVousResponseDTO;
import com.example.MomyCare.model.RendezVous;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RendezVousMapper {

    @Mapping(target = "patienteId",       source = "patiente.id")
    @Mapping(target = "patienteNom",      source = "patiente.user.nom")
    @Mapping(target = "patientePrenom",   source = "patiente.user.prenom")
    @Mapping(target = "gynecologueId",    source = "gynecologue.id")
    @Mapping(target = "gynecologueNom",   source = "gynecologue.user.nom")
    @Mapping(target = "gynecologuePrenom",source = "gynecologue.user.prenom")
    RendezVousResponseDTO toDto(RendezVous rendezVous);

    List<RendezVousResponseDTO> toDtoList(List<RendezVous> rdvList);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "statusRDV",   ignore = true)  // mis à EN_ATTENTE dans le service
    @Mapping(target = "patiente",    ignore = true)  // récupéré via Authentication
    @Mapping(target = "gynecologue", ignore = true)  // récupéré via gynecologueId
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    RendezVous toEntity(RendezVousRequestDTO rendezVousRequestDTO);

}
