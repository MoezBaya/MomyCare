package com.example.MomyCare.service;

import com.example.MomyCare.dto.Disponibilite.DisponibiliteDTO;
import com.example.MomyCare.dto.Disponibilite.DisponibiliteRequestDTO;

import java.util.List;

public interface DisponibiliteService {
    List<DisponibiliteDTO> getDisponibilitesParGyneco(Long gynecologueId);
    List<DisponibiliteDTO> getMesDisponibilites(Long gynecologueId);
    DisponibiliteDTO getDisponibiliteById(Long id);
    DisponibiliteDTO creerDisponibilite(Long gynecologueId, DisponibiliteRequestDTO dto);
    DisponibiliteDTO mettreAJourDisponibilite(Long id, Long gynecologueId, DisponibiliteRequestDTO dto);
    void deleteDisponibilite(Long id, Long gynecologueId);
}