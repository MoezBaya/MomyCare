package com.example.MomyCare.dao;

import com.example.MomyCare.model.Disponibilite;
import com.example.MomyCare.model.Gynecologue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByGynecologueAndDisponibleTrueOrderByDateTimeAsc(Gynecologue gynecologue);
    List<Disponibilite> findByGynecologueAndDisponibleTrueAndDateTimeAfterOrderByDateTimeAsc(Gynecologue gynecologue, LocalDateTime dateTime);
    List<Disponibilite> findByGynecologueOrderByDateTimeAsc(Gynecologue gynecologue);
    boolean existsByGynecologueAndDateTime(Gynecologue gynecologue, LocalDateTime dateTime);
    Optional<Disponibilite> findByGynecologueAndDateTime(Gynecologue gynecologue, LocalDateTime dateTime);
}

