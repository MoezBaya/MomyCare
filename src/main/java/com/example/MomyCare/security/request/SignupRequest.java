package com.example.MomyCare.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotBlank @Size(min = 3, max = 20)
    private String login;
    @NotBlank @Email @Size(max = 50)
    private String email;
    @NotBlank @Size(min = 6, max = 40)
    private String password;
    private String nom;
    private String prenom;
    private String adresse;
    private String ville;
    @NotBlank
    private String numeroTelephone;
    private LocalDate dateDeNaissance;
}