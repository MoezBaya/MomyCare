package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(unique = true)
    private LocalDateTime dateTime;

    private boolean disponible = true;

    @ManyToOne
    @JoinColumn(name = "gynecologue_id")
    private Gynecologue gynecologue;
}
