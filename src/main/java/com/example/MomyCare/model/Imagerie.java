package com.example.MomyCare.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Imagerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long referenceImagerie;

    private LocalDate dateImagerie;
    @Enumerated(EnumType.STRING)
    private TypeImagerie typeImagerie;

    @Column(name = "chemin_image", length = 500)
    private String cheminImage;

    @Column(nullable = false)
    private String centreRadiologie;

    @Column(nullable = false)
    private String medecinRadiologue;

    @Column(nullable = false)
    private String cliche;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

}
