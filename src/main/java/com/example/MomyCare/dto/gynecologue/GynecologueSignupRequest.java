package com.example.MomyCare.dto.gynecologue;

import com.example.MomyCare.security.request.SignupRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GynecologueSignupRequest extends SignupRequest {
    @NotNull
    private Long matriculeCachet;
    @NotBlank
    private String numeroAgrement;
    @Min(0)
    private Integer experience;
}
