package com.example.MomyCare.security.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private Long id;
    private String login;
    private String email;
    private List<String> roles;
    private String token;
    private String nom;

    private String prenom;

    private String adresse;

    private String ville;

    private String numeroTelephone;

    private LocalDate dateDeNaissance;
}