package com.ovapal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "medications")
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicineid;
    private Long userId;
    private String medicine;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
} 