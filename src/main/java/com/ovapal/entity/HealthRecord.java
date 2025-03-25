package com.ovapal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "health_records")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "healthid")
    private Long healthId;
    @Column(name = "userid")
    private Long userId;
    private LocalDate recordDate;
    private Double weight;
    private Double height;
    private Double temperature;
    private Integer heartRate;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private String notes;
} 