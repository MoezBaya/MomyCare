package com.example.MomyCare.security.jwt;

import com.example.MomyCare.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * ✅ CORRECTED: JwtUtils
 * - Fixed httpOnly=false to httpOnly=true (XSS vulnerability)
 * - Added Secure flag for HTTPS
 * - Added SameSite for CSRF protection
 * - Removed unnecessary System.out.println
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtCookieName:jwt-token}")
    private String jwtCookie;

    /**
     * Extract JWT from Authorization header (Bearer token)
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header received");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Remove "Bearer " prefix
        }
        return null;
    }

    /**
     * Extract JWT from HTTP cookie
     */
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            logger.debug("JWT cookie found");
            return cookie.getValue();
        }
        return null;
    }

    /**
     * ✅ FIXED: Generate JWT cookie with security improvements
     * - httpOnly=true prevents JavaScript from accessing the cookie (XSS protection)
     * - secure=true enforces HTTPS transmission
     * - sameSite=Strict prevents CSRF attacks
     */
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie
                .from(jwtCookie, jwt)
                .path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(true)        // ✅ FIXED: Prevent XSS by blocking JavaScript access
                .secure(true)          // ✅ FIXED: HTTPS only
                .sameSite("Strict")    // ✅ FIXED: CSRF protection
                .build();
        return cookie;
    }

    /**
     * Clear JWT cookie (logout)
     */
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie
                .from(jwtCookie, null)
                .path("/api")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    /**
     * Generate JWT token from username (login field)
     */
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username from JWT token
     */
    public String getUserNameFromJWTToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Generate HMAC signing key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    /**
     * ✅ IMPROVED: Validate JWT token with specific exception handling
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            logger.debug("JWT token validated successfully");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token format: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error validating JWT token", e);
        }
        return false;
    }
}
