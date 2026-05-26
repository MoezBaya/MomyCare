package com.example.MomyCare.security.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * ✅ CORRECTED: UserInfoResponse DTO
 * - Fixed type mismatch: List<String> roles instead of String role
 * - Added @Builder for flexibility
 * - Properly represents multiple roles
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private Long id;
    private String username;  // This is the "login" field from User entity
    private String email;
    private List<String> roles;  // ✅ FIXED: List instead of String to support multiple roles
}
