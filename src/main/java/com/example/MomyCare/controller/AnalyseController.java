package com.example.MomyCare.controller;

import com.example.MomyCare.dto.Analyse.AnalyseRequestDTO;
import com.example.MomyCare.dto.Analyse.AnalyseResponseDTO;
import com.example.MomyCare.service.AnalyseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/consultations/{id}/analyses")
@RequiredArgsConstructor
public class AnalyseController {

    private final AnalyseService analyseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalyseResponseDTO add(
            Authentication auth,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("dto") String dtoJson
    ) throws com.fasterxml.jackson.core.JsonProcessingException {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                new com.fasterxml.jackson.databind.ObjectMapper();
        AnalyseRequestDTO dto = objectMapper.readValue(dtoJson, AnalyseRequestDTO.class);
        return analyseService.addAnalyse(auth, id, dto, file);
    }

    @GetMapping
    public List<AnalyseResponseDTO> get(Authentication auth , @PathVariable Long id) {
        return analyseService.getByConsultation(auth ,id);
    }
}
