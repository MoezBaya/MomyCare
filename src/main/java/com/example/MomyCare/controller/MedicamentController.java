package com.example.MomyCare.controller;

import com.example.MomyCare.dto.medicament.MedicamentRequestDTO;
import com.example.MomyCare.dto.medicament.MedicamentResponseDTO;
import com.example.MomyCare.service.MedicamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicaments")
@RequiredArgsConstructor
public class MedicamentController {

    private final MedicamentService medicamentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENTE')")
    public ResponseEntity<List<MedicamentResponseDTO>> getAll() {
        return ResponseEntity.ok(medicamentService.getAllMedicaments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GYNECOLOGUE', 'PATIENTE')")
    public ResponseEntity<MedicamentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentService.getMedicamentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<MedicamentResponseDTO> create(
            @Valid @RequestBody MedicamentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicamentService.createMedicament(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<MedicamentResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MedicamentRequestDTO dto) {
        return ResponseEntity.ok(medicamentService.updateMedicament(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GYNECOLOGUE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicamentService.deleteMedicament(id);
        return ResponseEntity.noContent().build();
    }
}