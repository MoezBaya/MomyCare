package com.example.MomyCare.utils;

import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        String username = authentication.getName();
        if (username == null || username.equals("anonymousUser")) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));
    }

    public String loggedInEmail() {
        return getAuthenticatedUser().getEmail();
    }

    public Long loggedInUserId() {
        return getAuthenticatedUser().getId();
    }

    public User loggedInUser() {
        return getAuthenticatedUser();
    }
}