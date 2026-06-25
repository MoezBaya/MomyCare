package com.example.MomyCare.mapper;

import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;

import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteUpdateRequest;
import com.example.MomyCare.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromGynecoDto(GynecologueSignupRequest dto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromPatienteDto(PatienteUpdateRequest dto , @MappingTarget User user);
}
