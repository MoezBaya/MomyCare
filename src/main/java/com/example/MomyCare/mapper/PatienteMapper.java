package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteResponseDTO;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.RendezVous;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PatienteMapper {

    @Mapping(source = "user.nom", target = "nom")
    @Mapping(source = "user.prenom", target = "prenom")
    @Mapping(source = "user.adresse", target = "adresse")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.numeroTelephone", target = "numeroTelephone")
    @Mapping(source = "user.ville", target = "ville")

    @Mapping(target = "rendezVousIds", expression = "java(mapRendezVousIds(patiente))")
    @Mapping(target = "dossierMedicaleId", expression = "java(mapDossierId(patiente))")
    PatienteResponseDTO toDto(Patiente patiente);

    // ===== CREATE =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dossierMedicale", ignore = true)
    @Mapping(target = "rendezVousList", ignore = true)
    Patiente toEntity(PatienteSignupRequest dto);

    // ===== UPDATE =====
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(PatienteSignupRequest dto, @MappingTarget Patiente entity);

    // ===== HELPERS =====
    default List<Long> mapRendezVousIds(Patiente patiente) {
        if (patiente == null || patiente.getRendezVousList() == null) {
            return Collections.emptyList();
        }

        return patiente.getRendezVousList()
                .stream()
                .map(RendezVous::getId)
                .toList();
    }

    default Long mapDossierId(Patiente patiente) {
        if (patiente == null || patiente.getDossierMedicale() == null) {
            return null;
        }

        return patiente.getDossierMedicale().getNumeroDossier();
    }

    List<PatienteResponseDTO> toDTOList(List<Patiente> entity);
}
