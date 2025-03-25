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
public class PeriodRecordResponseBean {
    private Long periodRecId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String flow;  // Light, Medium, Heavy
    private String symptoms;
    private String mood;
    private String notes;
} 