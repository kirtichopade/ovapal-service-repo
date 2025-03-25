package com.ovapal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "period_records")
public class PeriodRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long periodrecid;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String flow;
    private String symptoms;
    private String mood;
    private String notes;
} 