package com.example.MomyCare.service;

import com.example.MomyCare.security.request.LoginRequest;
import com.example.MomyCare.security.request.SignupRequest;
import com.example.MomyCare.security.response.MessageResponse;
import com.example.MomyCare.security.response.UserInfoResponse;
import org.springframework.security.core.Authentication;

/**
 * ✅ CORRECTED: AuthService interface
 * - Fixed wrong Authentication import (was org.apache.tomcat...ciphers.Authentication)
 * - Now uses correct org.springframework.security.core.Authentication
 * - Clean method contracts
 */
public interface AuthService {

    /**
     * Authenticate user with login and password
     */
    UserInfoResponse login(LoginRequest request);

    /**
     * Register new user
     */
    MessageResponse register(SignupRequest request);

    /**
     * Get current authenticated user info
     */
    UserInfoResponse getCurrentUser(Authentication auth);

    /**
     * Logout current user
     */
    MessageResponse logout();
}
