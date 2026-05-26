package com.example.MomyCare.dao;

import com.example.MomyCare.model.Gynecologue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GynecologueRepository extends JpaRepository<Gynecologue, Long> {
    boolean existsByUserId(Long id);

    Optional<Gynecologue> findByUserId(Long id);
}
