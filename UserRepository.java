package com.example.MomyCare.dao;

import com.example.MomyCare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ✅ CORRECTED: UserRepository
 * - Fixed method names to match actual User entity fields (login, email)
 * - Removed non-existent "userName" references
 * - Clean method naming conventions
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ FIXED: Use "login" field name that actually exists in User entity
    Optional<User> findByLogin(String login);

    // ✅ FIXED: Use "email" field name that actually exists in User entity
    Optional<User> findByEmail(String email);

    // ✅ FIXED: Use "login" field for existence check
    boolean existsByLogin(String login);

    // ✅ FIXED: Use "email" field for existence check
    boolean existsByEmail(String email);
}
