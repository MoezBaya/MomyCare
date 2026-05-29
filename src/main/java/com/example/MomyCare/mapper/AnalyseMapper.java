package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.Analyse.AnalyseRequestDTO;
import com.example.MomyCare.dto.Analyse.AnalyseResponseDTO;
import com.example.MomyCare.model.AnalyseLaboratoire;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnalyseMapper {

    AnalyseLaboratoire toEntity(AnalyseRequestDTO dto);

    AnalyseResponseDTO toDto(AnalyseLaboratoire entity);

    List<AnalyseResponseDTO> toDtoList(List<AnalyseLaboratoire> list);
}
