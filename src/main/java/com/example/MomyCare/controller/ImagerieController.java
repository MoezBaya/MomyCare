package com.example.MomyCare.controller;

import com.example.MomyCare.dto.Imagerie.ImagerieRequestDTO;
import com.example.MomyCare.dto.Imagerie.ImagerieResponseDTO;
import com.example.MomyCare.service.ImagerieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/consultations/{id}/imageries")
@RequiredArgsConstructor
public class ImagerieController {

    private final ImagerieService imagerieService;

    @PostMapping
    public ImagerieResponseDTO add(
            Authentication auth ,
            @PathVariable Long id,
            @RequestPart MultipartFile file,
            @RequestPart ImagerieRequestDTO dto
    ) {
        return imagerieService.addImagerie(auth ,id, dto, file);
    }

    @GetMapping
    public List<ImagerieResponseDTO> get(Authentication auth , @PathVariable Long id) {
        return imagerieService.getByConsultation(auth ,id);
    }
}
