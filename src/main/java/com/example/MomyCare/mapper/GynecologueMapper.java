package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.gynecologue.GynecologueResponseDTO;
import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.model.Disponibilite;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.RendezVous;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GynecologueMapper {


        // ===== TO DTO =====
        @Mapping(source = "user.nom", target = "nom")
        @Mapping(source = "user.prenom", target = "prenom")
        @Mapping(source = "user.adresse", target = "adresse")
        @Mapping(source = "user.numeroTelephone", target = "numeroTelephone")
        @Mapping(source = "user.ville", target = "ville")
        @Mapping(source = "user.dateDeNaissance", target = "dateDeNaissance")
        @Mapping(source = "user.email", target = "email")

        @Mapping(source = "rendezVousList", target = "rendezVousIds")
        @Mapping(source = "disponibilites", target = "disponibiliteIds")
        GynecologueResponseDTO toDto(Gynecologue gynecologue);

        // ===== TO DTO LIST
         @Mapping(source = "user.nom" , target = "nom")
         @Mapping(source = "user.prenom" , target ="prenom")
         @Mapping(source = "user.adresse" , target ="adresse")
         @Mapping(source = "user.numeroTelephone" , target = "numeroTelephone")
         @Mapping(source = "user.ville" , target = "ville")
         @Mapping(source = "user.dateDeNaissance" , target = "dateDeNaissance" )
         @Mapping(source = "user.email", target = "email")

         @Mapping(source = "rendezVousList", target = "rendezVousIds")
         @Mapping(source = "disponibilites", target = "disponibiliteIds")
         List<GynecologueResponseDTO> toDtosList(List<Gynecologue> gynecologues);


        // ===== TO ENTITY =====
        @Mapping(target = "id", ignore = true)

        // MAP USER FIELDS
        @Mapping(source = "nom", target = "user.nom")
        @Mapping(source = "prenom", target = "user.prenom")
        @Mapping(source = "adresse", target = "user.adresse")
        @Mapping(source = "ville", target = "user.ville")
        @Mapping(source = "numeroTelephone", target = "user.numeroTelephone")
        @Mapping(source = "dateDeNaissance", target = "user.dateDeNaissance")
        @Mapping(source = "email", target = "user.email")
        @Mapping(source = "login", target = "user.login")

        @Mapping(target = "user.password", ignore = true)
        @Mapping(target = "user.roles", ignore = true)

        @Mapping(target = "rendezVousList", ignore = true)
        @Mapping(target = "disponibilites", ignore = true)
        @Mapping(target = "relations", ignore = true)
        @Mapping(target = "patiente", ignore = true)

        Gynecologue toEntity(GynecologueSignupRequest dto);



    // ===== UPDATE =====
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "numeroAgrement", ignore = true)
    @Mapping(target = "rendezVousList", ignore = true)
    @Mapping(target = "disponibilites", ignore = true)
    void updateFromDto(GynecologueSignupRequest dto, @MappingTarget Gynecologue entity);


    // ===== CUSTOM MAPPERS =====
    default List<Long> map(List<RendezVous> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(RendezVous::getId).toList();
    }

    default List<Long> mapDisponibilites(List<Disponibilite> list) {
        if (list == null) return Collections.emptyList();
        return list.stream().map(Disponibilite::getId).toList();
    }
}
