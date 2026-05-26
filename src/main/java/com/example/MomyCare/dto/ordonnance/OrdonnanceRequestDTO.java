package com.example.MomyCare.dto.ordonnance;

import com.example.MomyCare.dto.ligneOrdonnance.LigneOrdonnanceRequestDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdonnanceRequestDTO {

    private String numOrdonnance;

    private String cachets;

    private String signature;

    private List<LigneOrdonnanceRequestDTO> lignes;
}