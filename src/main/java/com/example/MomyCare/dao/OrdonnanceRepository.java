package com.example.MomyCare.dao;

import com.example.MomyCare.model.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {

    List<Ordonnance> findByConsultation_IdConsultation(Long consultationId);

    boolean existsByNumOrdonnanceAndConsultation_IdConsultation(
            String numOrdonnance, Long consultationId);
}
