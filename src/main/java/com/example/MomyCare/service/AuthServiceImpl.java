package com.example.MomyCare.service;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.RoleRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
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

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatienteRepository patienteRepository;
    private final GynecologueRepository gynecologueRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    public ResponseEntity<UserInfoResponse> login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getLogin(),
                                request.getPassword()
                        )
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = findUserById(userDetails.getId());

        String token = jwtUtils.generateTokenFromUsername(user.getLogin());

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(buildUserResponse(user, token));
    }

    // =========================================================
    // CURRENT USER
    // =========================================================

    @Override
    public UserInfoResponse getCurrentUser(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        User user = findUserById(userDetails.getId());

        return buildUserResponse(user, null);
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    @Override
    public ResponseEntity<MessageResponse> logout() {

        ResponseCookie cleanCookie = jwtUtils.getCleanJwtCookie();
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(new MessageResponse("Déconnexion réussie"));
    }

    // =========================================================
    // REGISTER PATIENTE
    // =========================================================

    @Override
    public ResponseEntity<?> registerPatiente(PatienteSignupRequest request) {

        validateUserRegistration(request);
        User user = buildUser(request);
        user.addRole(getRole(RoleName.ROLE_PATIENTE));
        userRepository.save(user);

        Patiente patiente = Patiente.builder()
                .user(user)
                .matriculeSociale(request.getMatriculeSociale())
                .build();

        patienteRepository.save(patiente);

        return ResponseEntity.ok(new MessageResponse("Inscription patiente réussie"));
    }

    // =========================================================
    // REGISTER GYNECOLOGUE
    // =========================================================

    @Override
    public ResponseEntity<?> registerGynecologue(GynecologueSignupRequest request) {

        validateUserRegistration(request);
        User user = buildUser(request);
        user.addRole(getRole(RoleName.ROLE_GYNECOLOGUE));
        userRepository.save(user);

        Gynecologue gynecologue = Gynecologue.builder()
                .user(user)
                .matriculeCachet(request.getMatriculeCachet())
                .numeroAgrement(request.getNumeroAgrement())
                .experience(request.getExperience())
                .build();

        gynecologueRepository.save(gynecologue);

        return ResponseEntity.ok(new MessageResponse("Inscription gynécologue réussie"));
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private void validateUserRegistration(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Login déjà utilisé");
        }
    }

    private User buildUser(SignupRequest request) {

        return User.builder()
                .login(request.getLogin())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .adresse(request.getAdresse())
                .ville(request.getVille())
                .numeroTelephone(request.getNumeroTelephone())
                .dateDeNaissance(request.getDateDeNaissance())
                .build();
    }

    private Role getRole(RoleName roleName) {

        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role introuvable : " + roleName));
    }

    private User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private UserInfoResponse buildUserResponse(User user, String token) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .map(Enum::name)
                .toList();

        return UserInfoResponse.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())

                .nom(user.getNom())
                .prenom(user.getPrenom())
                .adresse(user.getAdresse())
                .ville(user.getVille())
                .numeroTelephone(user.getNumeroTelephone())
                .dateDeNaissance(user.getDateDeNaissance())

                .roles(roles)
                .token(token)
                .build();
    }
}