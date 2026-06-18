package com.example.MomyCare.service.impl;

import com.example.MomyCare.dao.GynecologueRepository;
import com.example.MomyCare.dao.PatienteRepository;
import com.example.MomyCare.dao.RoleRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.dto.gynecologue.GynecologueSignupRequest;
import com.example.MomyCare.dto.patiente.PatienteSignupRequest;
import com.example.MomyCare.exception.BadRequestException;
import com.example.MomyCare.exception.ConflictException;
import com.example.MomyCare.exception.ResourceNotFoundException;
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
import com.example.MomyCare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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



    @Override
    public ResponseEntity<UserInfoResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = findUserById(userDetails.getId());
            String token = jwtUtils.generateToken(user.getLogin());
            return ResponseEntity.ok().body(buildUserResponse(user, token));
        } catch (AuthenticationException e) {
            throw new BadRequestException("Login ou mot de passe incorrect");
        }
    }



    @Override
    public UserInfoResponse getCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("Utilisateur non authentifié");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = findUserById(userDetails.getId());
        return buildUserResponse(user, null);
    }

    @Override
    public ResponseEntity<MessageResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body(new MessageResponse("Déconnexion réussie"));
    }


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

    @Override
    public Patiente registerPatienteAndReturnEntity(PatienteSignupRequest request) {
        validateUserRegistration(request);
        User user = buildUser(request);
        user.addRole(getRole(RoleName.ROLE_PATIENTE));
        user = userRepository.save(user);

        Patiente patiente = Patiente.builder()
                .user(user)
                .matriculeSociale(request.getMatriculeSociale())
                .build();
        return patienteRepository.save(patiente);
    }


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

    private void validateUserRegistration(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email déjà utilisé");
        }
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new ConflictException("Login déjà utilisé");
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
                .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable : " + roleName));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));
    }

    private UserInfoResponse buildUserResponse(User user, String token) {
        List<String> roles = user.getRoles().stream()
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