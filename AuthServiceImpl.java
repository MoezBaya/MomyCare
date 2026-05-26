package com.example.MomyCare.service;

import com.example.MomyCare.dao.RoleRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.model.Role;
import com.example.MomyCare.model.RoleName;
import com.example.MomyCare.model.User;
import com.example.MomyCare.security.jwt.JwtUtils;
import com.example.MomyCare.security.request.LoginRequest;
import com.example.MomyCare.security.request.SignupRequest;
import com.example.MomyCare.security.response.MessageResponse;
import com.example.MomyCare.security.response.UserInfoResponse;
import com.example.MomyCare.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ✅ CORRECTED: AuthServiceImpl implementation
 * - Fixed missing imports (GrantedAuthority)
 * - Proper role handling with ManyToMany support
 * - Better error handling and validation
 * - Clean separation of concerns
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public UserInfoResponse login(LoginRequest request) {
        // Authenticate using either login or email
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getLogin(),
                    request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Generate JWT token
        String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        // ✅ FIXED: Added missing import for GrantedAuthority
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // ✅ FIXED: Return proper UserInfoResponse with List<String> roles and token
        return UserInfoResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .token(jwt)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse register(SignupRequest request) {
        // Validate login uniqueness
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Login is already in use");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user with encoded password
        User user = new User();
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        // ✅ FIXED: Assign role with ManyToMany support
        Role role = roleRepository.findByRoleName(RoleName.valueOf(request.getRole().toUpperCase()))
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));

        // Initialize roles set and add the role
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(role);
        user.setRoles(userRoles);

        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

    @Override
    public UserInfoResponse getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        // ✅ FIXED: Proper list of roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return UserInfoResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    public MessageResponse logout() {
        SecurityContextHolder.clearContext();
        return new MessageResponse("Logged out successfully!");
    }
}
