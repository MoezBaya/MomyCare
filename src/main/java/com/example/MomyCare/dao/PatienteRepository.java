package com.example.MomyCare.dao;

import com.example.MomyCare.model.Patiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatienteRepository extends JpaRepository<Patiente, Long> {
    Optional<Patiente> findByUserId(Long userId);

    boolean existsByUserId(Long id);

    List<Patiente> findByGynecologueId(Long id);
}
