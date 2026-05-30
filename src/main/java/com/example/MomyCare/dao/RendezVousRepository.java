package com.example.MomyCare.dao;

import com.example.MomyCare.model.Gynecologue;
import com.example.MomyCare.model.Patiente;
import com.example.MomyCare.model.RendezVous;
import com.example.MomyCare.model.StatusRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByGynecologueAndStatusRDV(Gynecologue g, StatusRDV s);
    List<RendezVous> findByPatiente(Patiente p);
    boolean existsByPatienteAndGynecologueAndStatusRDV(Patiente p, Gynecologue g, StatusRDV s);
    @Query("SELECT r FROM RendezVous r WHERE r.gynecologue.id = :gynecoId AND r.statusRDV = :status")
    List<RendezVous> findByGynecologueIdAndStatusRDV(@Param("gynecoId") Long gynecoId, @Param("status") StatusRDV status);
}
