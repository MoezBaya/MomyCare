package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.model.Disponibilite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DisponibiliteMapper {

    @Mapping(source = "gynecologue.id", target = "gynecologueId")
    DisponibiliteDTO toDto(Disponibilite disponibilite);

    List<DisponibiliteDTO> toDtoList(List<Disponibilite> disponibilites);
}

