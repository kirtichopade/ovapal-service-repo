package com.ovapal.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecordRequestBean {
    private Long userId;
    private Long healthId;
    private LocalDate recordDate;
    private Double weight;
    private Double height;
    private Double temperature;
    private Integer heartRate;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private String notes;
} 