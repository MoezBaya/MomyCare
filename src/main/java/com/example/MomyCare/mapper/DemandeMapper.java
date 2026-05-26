package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.demande.DemandeResponseDTO;
import com.example.MomyCare.model.Demande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DemandeMapper {

    @Mapping(expression = "java(demande.getGynecologue() != null ? demande.getGynecologue().getId() : null)", target = "gynecologueId")
    @Mapping(expression = "java(demande.getGynecologue() != null && demande.getGynecologue().getUser() != null ? demande.getGynecologue().getUser().getLogin() : null)", target = "gynecologueLogin")
    @Mapping(expression = "java(demande.getPatiente() != null ? demande.getPatiente().getId() : null)", target = "patienteId")
    @Mapping(expression = "java(demande.getPatiente() != null && demande.getPatiente().getUser() != null ? demande.getPatiente().getUser().getLogin() : null)", target = "patienteLogin")
    DemandeResponseDTO toDTO(Demande demande);

    List<DemandeResponseDTO> toDTOList(List<Demande> demandes);
}