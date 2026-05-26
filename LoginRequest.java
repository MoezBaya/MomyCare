package com.example.MomyCare.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ CORRECTED: LoginRequest DTO
 * - Removed unnecessary @Component annotation
 * - Removed unnecessary imports (Tomcat, Hibernate)
 * - Clean, minimal DTO design
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Login is required")
    private String login;  // Can be username or email (handled by UserDetailsService)

    @NotBlank(message = "Password is required")
    private String password;
}
