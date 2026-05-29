package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.Imagerie.ImagerieRequestDTO;
import com.example.MomyCare.dto.Imagerie.ImagerieResponseDTO;
import com.example.MomyCare.model.Imagerie;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImagerieMapper {

    Imagerie toEntity(ImagerieRequestDTO dto);

    ImagerieResponseDTO toDto(Imagerie entity);

    List<ImagerieResponseDTO> toDtoList(List<Imagerie> list);
}
