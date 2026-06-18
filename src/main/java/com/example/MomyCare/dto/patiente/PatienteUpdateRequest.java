package com.example.MomyCare.dto.patiente;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatienteUpdateRequest {
    private String nom;
    private String prenom;
    private String email;
    private String numeroTelephone;
    private String ville;
    private String adresse;
    private Long matriculeSociale;
}
