package com.example.MomyCare.dao;

import com.example.MomyCare.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation , Long> {
    List<Consultation> findByDossierMedicale_Patiente_Id(Long patienteId );
}
