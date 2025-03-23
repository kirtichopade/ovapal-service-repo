package com.ovapal.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderRequestBean {
    private Long userId;
    private String title;
    private String description;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private Boolean isRepeating;
    private String repeatFrequency;
    private Boolean isActive;
} 