package com.example.MomyCare.dto.patiente;

import com.example.MomyCare.security.request.SignupRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PatienteSignupRequest extends SignupRequest {
    @NotNull
    private Long matriculeSociale;
}
