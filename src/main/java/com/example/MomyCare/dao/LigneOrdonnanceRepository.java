package com.example.MomyCare.dao;

import com.example.MomyCare.model.LigneOrdonnance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneOrdonnanceRepository extends JpaRepository<LigneOrdonnance, Long> {
    List<LigneOrdonnance> findByOrdonnanceIdOrdonnance(Long ordonnanceId);
}
