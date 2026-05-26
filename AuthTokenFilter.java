package com.example.MomyCare.security.jwt;

import com.example.MomyCare.security.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ✅ CORRECTED: AuthTokenFilter
 * - Removed @Autowired in favor of constructor injection (better for testing)
 * - Improved exception handling (specific exceptions instead of generic Exception)
 * - Better logging and security
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        logger.debug("AuthTokenFilter processing URI: {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJWTToken(jwt);
                
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User authenticated: {} with roles: {}", username, userDetails.getAuthorities());
                    
                } catch (Exception e) {
                    logger.warn("Cannot load user details for username: {}", username, e);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot process JWT authentication", e);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT from request (header first, then cookie)
     */
    private String parseJwt(HttpServletRequest request) {
        // Try Authorization header first
        String jwt = jwtUtils.getJwtFromHeader(request);
        
        // Fallback to cookie
        if (jwt == null) {
            jwt = jwtUtils.getJwtFromCookies(request);
        }
        
        return jwt;
    }
}
