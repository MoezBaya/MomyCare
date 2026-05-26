package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
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
    private LocalDateTime dateRendezVous ;

    @Enumerated(EnumType.STRING)
    private StatusRDV statusRDV ;

    @Enumerated(EnumType.STRING)
    private MotifRendezVous motif;

    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "patiente_id")
    private Patiente patiente;

    @ManyToOne
    @JoinColumn(name = "gynecologue_id")
    private Gynecologue gynecologue;

}
