package com.example.MomyCare.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        logger.info("=== AUTH FILTER START ===");
        logger.info("Method: {}, URI: {}", method, uri);

        // Log all headers for debugging
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerName.equalsIgnoreCase("authorization") || headerName.equalsIgnoreCase("cookie")) {
                logger.info("Header - {}: {}", headerName, headerValue != null ? headerValue.substring(0, Math.min(100, headerValue.length())) : "null");
            }
        }

        // Public endpoints - ne pas vérifier l'authentification
        if (isPublicEndpoint(uri)) {
            logger.info("Public endpoint, skipping authentication: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Essayer de récupérer le JWT
            String jwt = parseJwt(request);
            logger.info("JWT extracted: {}", jwt != null ? "PRESENT (length: " + jwt.length() + ")" : "NULL");

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJWTToken(jwt);
                logger.info("Username from JWT: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("User authenticated successfully: {}", username);
            } else {
                logger.warn("No valid JWT token found for URI: {}", uri);
                // Ne pas jeter d'exception, laisser le filtre continuer
                // Le AuthEntryPointJwt gérera l'erreur si nécessaire
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        logger.info("=== AUTH FILTER END ===");
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        // Essayer d'abord les cookies
        String jwt = jwtUtils.getJwtFromCookies(request);
        logger.info("JWT from cookies: {}", jwt != null ? "PRESENT" : "NULL");

        // Si pas trouvé, essayer l'Authorization header
        if (jwt == null) {
            jwt = jwtUtils.getJwtFromHeader(request);
            logger.info("JWT from header: {}", jwt != null ? "PRESENT" : "NULL");
        }

        return jwt;
    }

    private boolean isPublicEndpoint(String uri) {
        boolean isPublic = uri.startsWith("/api/auth/signin") ||
                uri.startsWith("/api/auth/signup") ||
                uri.startsWith("/api/auth/signout") ||
                uri.startsWith("/api/public") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/h2-console") ||
                uri.startsWith("/error") ||
                uri.equals("/");

        if (isPublic) {
            logger.debug("Endpoint is public: {}", uri);
        }

        return isPublic;
    }
}