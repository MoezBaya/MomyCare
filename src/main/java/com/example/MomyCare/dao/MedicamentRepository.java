package com.example.MomyCare.dao;

import com.example.MomyCare.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament , Long> {
}
