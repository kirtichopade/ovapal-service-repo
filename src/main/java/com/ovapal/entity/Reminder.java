package com.ovapal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderid;
    private Long userId;
    private String title;
    private String description;
    private LocalDate reminderDate;
    private LocalTime reminderTime;
    private Boolean isRepeating;
    private String repeatFrequency;
    private Boolean isActive;
} 