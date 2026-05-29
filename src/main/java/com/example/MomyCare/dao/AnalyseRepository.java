package com.example.MomyCare.dao;

import com.example.MomyCare.model.AnalyseLaboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyseRepository extends JpaRepository<AnalyseLaboratoire , Long> {
    List<AnalyseLaboratoire> findByConsultation_IdConsultation(Long consultationId);
}
