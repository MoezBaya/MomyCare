package com.example.MomyCare.dao;

import com.example.MomyCare.model.DossierMedicale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DossierMedicaleRepository extends JpaRepository<DossierMedicale, Long> {
    Optional<DossierMedicale> findByPatiente_Id(Long patienteId);
}
