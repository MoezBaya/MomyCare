package com.example.MomyCare.controller;

import com.example.MomyCare.dto.Imagerie.ImagerieRequestDTO;
import com.example.MomyCare.dto.Imagerie.ImagerieResponseDTO;
import com.example.MomyCare.service.ImagerieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/consultations/{id}/imageries")
@RequiredArgsConstructor
public class ImagerieController {

    private final ImagerieService imagerieService;
    private final ObjectMapper objectMapper;  // Inject ObjectMapper


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImagerieResponseDTO add(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,  // Changed to @RequestParam
            @RequestParam("dto") String dtoJson       // Receive as String
    ) throws Exception {
        ImagerieRequestDTO dto = objectMapper.readValue(dtoJson, ImagerieRequestDTO.class);
        return imagerieService.addImagerie(id, dto, file);
    }



    @GetMapping
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENTE')")
    public List<ImagerieResponseDTO> get(@PathVariable Long id) {
        return imagerieService.getByConsultation(id);
    }
}
