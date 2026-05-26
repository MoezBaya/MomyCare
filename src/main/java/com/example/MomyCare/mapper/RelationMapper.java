package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.relation.RelationResponseDTO;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.Relation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RelationMapper {

    @Mapping(target = "patienteId", source = "patiente.id")
    @Mapping(target = "patienteFullName", source = "patiente", qualifiedByName = "patienteToNom")

    @Mapping(target = "gynecologueId", source = "gynecologue.id")
    @Mapping(target = "gynecologueFullName", source = "gynecologue", qualifiedByName = "gynecologueToNom")
    @Mapping(target = "status", source = "status")
    RelationResponseDTO toDto(Relation relation);

    List<RelationResponseDTO> toDtoList(List<Relation> relations);

    @Named("patienteToNom")
    default String patienteToNom(Patiente patiente) {

        if (patiente == null || patiente.getUser() == null) {
            return null;
        }

        return patiente.getUser().getPrenom()
                + " "
                + patiente.getUser().getNom();
    }

    @Named("gynecologueToNom")
    default String gynecologueToNom(Gynecologue gynecologue) {

        if (gynecologue == null || gynecologue.getUser() == null) {
            return null;
        }

        return gynecologue.getUser().getPrenom()
                + " "
                + gynecologue.getUser().getNom();
    }
}