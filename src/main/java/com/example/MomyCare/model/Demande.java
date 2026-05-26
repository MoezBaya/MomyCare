package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Demande {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "patiente_id")
        private Patiente patiente;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "gynecologue_id")
        private Gynecologue gynecologue;

        @Enumerated(EnumType.STRING)
        private DemandeStatus status;

        private LocalDateTime createdAt;

        @PrePersist
        public void ajouterDate() {
                this.createdAt = LocalDateTime.now();
        }
}
