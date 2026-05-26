package com.example.MomyCare.security;

import com.example.MomyCare.dao.RoleRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.model.Role;
import com.example.MomyCare.model.RoleName;
import com.example.MomyCare.model.User;
import com.example.MomyCare.security.jwt.AuthEntryPointJwt;
import com.example.MomyCare.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * ✅ CORRECTED: WebSecurityConfig
 * - Completed CommandLineRunner initialization (was incomplete/cut off)
 * - Fixed role/user initialization for ManyToMany relationship
 * - Proper error handling and logging
 * - Uses correct role references
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizerequests -> authorizerequests
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/image/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-ressources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"
        ));
    }

    /**
     * ✅ CORRECTED: Initialize default roles and sample users
     * - Supports ManyToMany role assignment
     * - Proper role creation and user assignment
     * - Complete implementation (was cut off before)
     */
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, 
                                     UserRepository userRepository, 
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // Create roles if they don't exist
                Role userRole = roleRepository.findByRoleName(RoleName.ROLE_PATIENTE)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setRoleName(RoleName.ROLE_PATIENTE);
                            return roleRepository.save(newRole);
                        });

                Role gynecologueRole = roleRepository.findByRoleName(RoleName.ROLE_GYNECOLOGUE)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setRoleName(RoleName.ROLE_GYNECOLOGUE);
                            return roleRepository.save(newRole);
                        });

                Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setRoleName(RoleName.ROLE_ADMIN);
                            return roleRepository.save(newRole);
                        });

                // Create sample users if they don't exist
                if (!userRepository.existsByLogin("patiente1")) {
                    User patiente = new User();
                    patiente.setLogin("patiente1");
                    patiente.setEmail("patiente1@momycare.com");
                    patiente.setPassword(passwordEncoder.encode("SecurePass@1234"));
                    
                    Set<Role> patienteRoles = new HashSet<>();
                    patienteRoles.add(userRole);
                    patiente.setRoles(patienteRoles);
                    
                    userRepository.save(patiente);
                }

                if (!userRepository.existsByLogin("gynecologue1")) {
                    User gynecologue = new User();
                    gynecologue.setLogin("gynecologue1");
                    gynecologue.setEmail("gynecologue1@momycare.com");
                    gynecologue.setPassword(passwordEncoder.encode("SecurePass@1234"));
                    
                    Set<Role> gynecologueRoles = new HashSet<>();
                    gynecologueRoles.add(gynecologueRole);
                    gynecologue.setRoles(gynecologueRoles);
                    
                    userRepository.save(gynecologue);
                }

                if (!userRepository.existsByLogin("admin")) {
                    User admin = new User();
                    admin.setLogin("admin");
                    admin.setEmail("admin@momycare.com");
                    admin.setPassword(passwordEncoder.encode("AdminSecure@1234"));
                    
                    // Admin has all roles
                    Set<Role> adminRoles = new HashSet<>();
                    adminRoles.add(userRole);
                    adminRoles.add(gynecologueRole);
                    adminRoles.add(adminRole);
                    admin.setRoles(adminRoles);
                    
                    userRepository.save(admin);
                }
            } catch (Exception e) {
                System.err.println("Error initializing database: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
