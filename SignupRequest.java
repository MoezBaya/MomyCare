package com.example.MomyCare.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ CORRECTED: SignupRequest DTO
 * - Removed unnecessary @Component annotation
 * - Strengthened password validation (min 12 chars, complexity required)
 * - Removed unnecessary imports
 * - Added role validation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    private String login;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{12,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@#$%^&+=)"
    )
    private String password;

    @NotBlank(message = "Role is required")
    private String role;
}
