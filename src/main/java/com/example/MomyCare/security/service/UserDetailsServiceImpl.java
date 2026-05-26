package com.example.MomyCare.security.service;

import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String loginOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByLogin(loginOrEmail)
                .or(() -> userRepository.findByEmail(loginOrEmail))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with login/email: " + loginOrEmail)
                );

        return UserDetailsImpl.build(user);
    }
}