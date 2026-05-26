package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.consultation.ConsultationRequestDTO;
import com.example.MomyCare.dto.consultation.ConsultationResponseDTO;
import com.example.MomyCare.model.Consultation;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsultationMapper {

    @Mapping(target = "id",              source = "idConsultation")
    @Mapping(target = "dossierMedicaleId", source = "dossierMedicale.id")
    ConsultationResponseDTO toDto(Consultation consultation);

    List<ConsultationResponseDTO> toDtoList(List<Consultation> consultations);

    @Mapping(target = "idConsultation",  ignore = true)
    @Mapping(target = "dossierMedicale", ignore = true)
    @Mapping(target = "analyses",        ignore = true)
    @Mapping(target = "imageries",       ignore = true)
    @Mapping(target = "ordonnances",     ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    Consultation toEntity(ConsultationRequestDTO dto);

    void updateDtoFromEntity(ConsultationRequestDTO dto ,@MappingTarget Consultation consultation);
}