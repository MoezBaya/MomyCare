package com.example.MomyCare.security;

import com.example.MomyCare.security.jwt.AuthEntryPointJwt;
import com.example.MomyCare.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthTokenFilter authTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    ) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth


                        // AUTH
                        .requestMatchers("/api/auth/**").permitAll()

                        // Documentatio SWAGGER
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()

                        // DISPONIBILITES (IMPORTANT FIX)
                        .requestMatchers("/api/disponibilites/gyneco/**")
                        .hasAnyRole("PATIENTE", "GYNECOLOGUE")

                        .requestMatchers("/api/disponibilites/mes-disponibilites")
                        .hasRole("GYNECOLOGUE")

                        .requestMatchers("/api/disponibilites/**")
                        .hasRole("GYNECOLOGUE")


                        // ================= GYNECO PROFILE =================
                        .requestMatchers("/api/gynecologues/me").hasRole("GYNECOLOGUE")
                        .requestMatchers("/api/gynecologues/patientes").hasRole("GYNECOLOGUE")

                        // ================= DISPONIBILITES =================
                        .requestMatchers("/api/disponibilites/gyneco/**")
                        .hasAnyRole("GYNECOLOGUE", "PATIENTE")

                        .requestMatchers("/api/disponibilites/mes-disponibilites")
                        .hasRole("GYNECOLOGUE")

                        .requestMatchers("/api/disponibilites/**")
                        .hasRole("GYNECOLOGUE")

                        // ================= CONSULTATIONS =================
                        .requestMatchers("/api/consultations/**")
                        .hasAnyRole("GYNECOLOGUE", "PATIENTE")

                        // ================= DOSSIER =================
                        .requestMatchers("/api/dossiers/**")
                        .hasAnyRole("GYNECOLOGUE", "PATIENTE")

                        // ================= RDV =================
                        .requestMatchers("/api/rdv/**")
                        .hasAnyRole("GYNECOLOGUE", "PATIENTE")

                        // ================= ORDONNANCES =================
                        .requestMatchers("/api/consultations/*/ordonnances/**")
                        .hasAnyRole("GYNECOLOGUE", "PATIENTE")

                        // ================= MEDICAMENTS =================
                        .requestMatchers("/api/medicaments/**")
                        .hasAnyRole("GYNECOLOGUE", "PATIENTE")

                        // ====================== Relation ==================
                        .requestMatchers("/api/relations/mes-relations")
                        .hasRole("PATIENTE")

                        .requestMatchers("/api/relations/demandes")
                        .hasRole("GYNECOLOGUE")

                        .requestMatchers("/api/relations/*/terminer")
                        .hasRole("GYNECOLOGUE")

                        .requestMatchers("/api/relations/**")
                        .authenticated()

                        // ===================== PAtiente =================
                        .requestMatchers("/api/patientes/mes-patientes")
                        .hasRole("GYNECOLOGUE")

                        // ====================== Imagerie ======================/
                        .requestMatchers("/api/consultations/*/imageries")
                        .hasAnyRole("GYNECOLOGUE" , "PATIENTE")

                        // ====================   Test        ================ ///
                        .requestMatchers("/test/**").permitAll()
                        // ================= DEFAULT =================
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}