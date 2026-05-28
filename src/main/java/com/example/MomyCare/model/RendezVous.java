package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateRendezVous;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRDV statusRDV;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotifRendezVous motif;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patiente_id", nullable = false)
    private Patiente patiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gynecologue_id", nullable = false)
    private Gynecologue gynecologue;
}
