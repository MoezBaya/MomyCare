package com.example.MomyCare.dto.relation;

import com.example.MomyCare.model.StatutRelation;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelationResponseDTO {

   private Long id;

   private Long patienteId;
   private String patienteFullName;

   private Long gynecologueId;
   private String gynecologueFullName;

   private StatutRelation status;

   private LocalDateTime createdAt;
}
