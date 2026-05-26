package com.example.MomyCare.mapper;
import com.example.MomyCare.dto.DossierMedicale.CreateDossierMedicaleDTO;
import com.example.MomyCare.dto.DossierMedicale.DossierMedicaleResponseDTO;
import com.example.MomyCare.model.DossierMedicale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import com.example.MomyCare.model.Gynecologue;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DossierMedicalMapper {

    @Mapping(target = "patienteId", source = "patiente.id")
    @Mapping(target = "modifiePar", source = "derniereModificationPar", qualifiedByName = "gynecologueToNom")
    DossierMedicaleResponseDTO toDto(DossierMedicale dossierMedicale);

    List<DossierMedicaleResponseDTO> toDtoList(List<DossierMedicale> dossierMedicales);

    @Mapping(target = "patiente", ignore = true)
    @Mapping(target = "derniereModificationPar", ignore = true)
    @Mapping(target = "consultations", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DossierMedicale toEntity(CreateDossierMedicaleDTO dto);

    @Mapping(target = "patiente", ignore = true)
    @Mapping(target = "derniereModificationPar", ignore = true)
    @Mapping(target = "consultations", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(CreateDossierMedicaleDTO dto,
                             @MappingTarget DossierMedicale dossierMedicale);

    // ─── Helper: extrait le nom depuis Gynecologue ────────────────────────────
    @Named("gynecologueToNom")
    default String gynecologueToNom(Gynecologue gyneco) {
        if (gyneco == null) return null;

        return gyneco.getUser().getPrenom() + " " + gyneco.getUser().getNom();


    }
}