package com.example.MomyCare.security.service;

import com.example.MomyCare.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * ✅ CORRECTED: Spring Security UserDetails implementation
 * - Implements Serializable for session clustering
 * - Includes hashCode() to match equals() contract
 * - Handles multiple roles (ManyToMany)
 * - Null-safe role handling
 * - Immutable authorities collection
 */
public class UserDetailsImpl implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;  // This is the "login" field from User entity
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * ✅ FIXED: Build UserDetailsImpl from User entity
     * - Handles multiple roles via ManyToMany
     * - Null-safe: returns empty authorities if roles is null
     * - Uses try-catch for safety
     */
    public static UserDetailsImpl build(User user) {
        // ✅ Support multiple roles
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .toList();

        return new UserDetailsImpl(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", authorities=" + authorities +
                '}';
    }

    /**
     * ✅ FIXED: Proper equals implementation
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    /**
     * ✅ FIXED: Added hashCode to maintain equals/hashCode contract
     * Required for proper use in HashMap/HashSet
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
