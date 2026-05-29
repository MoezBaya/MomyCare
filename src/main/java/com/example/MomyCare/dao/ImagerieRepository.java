package com.example.MomyCare.dao;

import com.example.MomyCare.model.Imagerie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagerieRepository extends JpaRepository<Imagerie , Long> {
    List<Imagerie> findByConsultation_IdConsultation(Long consultationId);
}
