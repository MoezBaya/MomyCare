package com.example.MomyCare.controller;

import com.example.MomyCare.dto.Analyse.AnalyseRequestDTO;
import com.example.MomyCare.dto.Analyse.AnalyseResponseDTO;
import com.example.MomyCare.service.AnalyseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/consultations/{id}/analyses")
@RequiredArgsConstructor

@Tag(
        name = "Analyses",
        description = "Gestion des analyses médicales"
)

@SecurityRequirement(name = "bearerAuth")
public class AnalyseController {

    private final AnalyseService analyseService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Ajouter une analyse médicale",
            description = "Permet au gynécologue d’ajouter une analyse avec un fichier médical")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public AnalyseResponseDTO addAnalyse(

            @Parameter(description = "ID de la consultation")
            @PathVariable Long id, @Parameter(description = "Fichier de l’analyse",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Données JSON de l’analyse")
            @RequestParam("dto") String dtoJson

    ) throws JsonProcessingException {

        AnalyseRequestDTO dto =
                objectMapper.readValue(dtoJson, AnalyseRequestDTO.class);

        return analyseService.addAnalyse(id, dto, file);
    }

    @Operation(
            summary = "Récupérer les analyses d’une consultation",
            description = "Retourne la liste des analyses liées à une consultation"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENTE')")
    public List<AnalyseResponseDTO> getAnalyses(
            @Parameter(description = "ID de la consultation")
            @PathVariable Long id

    ) {
        return analyseService.getByConsultation(id);
    }
}