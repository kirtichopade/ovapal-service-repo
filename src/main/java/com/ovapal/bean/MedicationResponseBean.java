package com.ovapal.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationResponseBean {
    private Long medicationId;
    private Long userId;
    private String medicine;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
} 