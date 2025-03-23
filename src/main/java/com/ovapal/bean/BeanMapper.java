package com.ovapal.bean;

import com.ovapal.entity.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BeanMapper {

    // User Mappings
    public User toEntity(UserRequestBean userRequestBean) {
        return User.builder()
                .name(userRequestBean.getName())
                .email(userRequestBean.getEmail())
                .password(userRequestBean.getPassword())
                .age(userRequestBean.getAge())
                .build();
    }

    public UserResponseBean toBean(User user) {
        return UserResponseBean.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();
    }
    
    // LoginRequest Mapping
    public LoginRequest toEntity(LoginRequestBean loginRequestBean) {
        return LoginRequest.builder()
                .email(loginRequestBean.getEmail())
                .password(loginRequestBean.getPassword())
                .build();
    }
    
    // HealthRecord Mappings
    public HealthRecord toEntity(HealthRecordRequestBean healthRecordRequestBean) {
        return HealthRecord.builder()
                .userId(healthRecordRequestBean.getUserId())
                .recordDate(healthRecordRequestBean.getRecordDate())
                .weight(healthRecordRequestBean.getWeight())
                .height(healthRecordRequestBean.getHeight())
                .temperature(healthRecordRequestBean.getTemperature())
                .heartRate(healthRecordRequestBean.getHeartRate())
                .bloodPressureSystolic(healthRecordRequestBean.getBloodPressureSystolic())
                .bloodPressureDiastolic(healthRecordRequestBean.getBloodPressureDiastolic())
                .notes(healthRecordRequestBean.getNotes())
                .build();
    }
    
    public HealthRecordResponseBean toBean(HealthRecord healthRecord) {
        return HealthRecordResponseBean.builder()
                .id(healthRecord.getId())
                .userId(healthRecord.getUserId())
                .recordDate(healthRecord.getRecordDate())
                .weight(healthRecord.getWeight())
                .height(healthRecord.getHeight())
                .temperature(healthRecord.getTemperature())
                .heartRate(healthRecord.getHeartRate())
                .bloodPressureSystolic(healthRecord.getBloodPressureSystolic())
                .bloodPressureDiastolic(healthRecord.getBloodPressureDiastolic())
                .notes(healthRecord.getNotes())
                .build();
    }
    
    // PeriodRecord Mappings
    public PeriodRecord toEntity(PeriodRecordRequestBean periodRecordRequestBean) {
        return PeriodRecord.builder()
                .userId(periodRecordRequestBean.getUserId())
                .startDate(periodRecordRequestBean.getStartDate())
                .endDate(periodRecordRequestBean.getEndDate())
                .flow(periodRecordRequestBean.getFlow())
                .symptoms(periodRecordRequestBean.getSymptoms())
                .mood(periodRecordRequestBean.getMood())
                .notes(periodRecordRequestBean.getNotes())
                .build();
    }
    
    public PeriodRecordResponseBean toBean(PeriodRecord periodRecord) {
        return PeriodRecordResponseBean.builder()
                .id(periodRecord.getId())
                .userId(periodRecord.getUserId())
                .startDate(periodRecord.getStartDate())
                .endDate(periodRecord.getEndDate())
                .flow(periodRecord.getFlow())
                .symptoms(periodRecord.getSymptoms())
                .mood(periodRecord.getMood())
                .notes(periodRecord.getNotes())
                .build();
    }
    
    // Reminder Mappings
    public Reminder toEntity(ReminderRequestBean reminderRequestBean) {
        return Reminder.builder()
                .userId(reminderRequestBean.getUserId())
                .title(reminderRequestBean.getTitle())
                .description(reminderRequestBean.getDescription())
                .reminderDate(reminderRequestBean.getReminderDate())
                .reminderTime(reminderRequestBean.getReminderTime())
                .isRepeating(reminderRequestBean.getIsRepeating())
                .repeatFrequency(reminderRequestBean.getRepeatFrequency())
                .isActive(reminderRequestBean.getIsActive())
                .build();
    }
    
    public ReminderResponseBean toBean(Reminder reminder) {
        return ReminderResponseBean.builder()
                .id(reminder.getId())
                .userId(reminder.getUserId())
                .title(reminder.getTitle())
                .description(reminder.getDescription())
                .reminderDate(reminder.getReminderDate())
                .reminderTime(reminder.getReminderTime())
                .isRepeating(reminder.getIsRepeating())
                .repeatFrequency(reminder.getRepeatFrequency())
                .isActive(reminder.getIsActive())
                .build();
    }
    
    // Medication Mappings
    public Medication toEntity(MedicationRequestBean medicationRequestBean) {
        return Medication.builder()
                .userId(medicationRequestBean.getUserId())
                .medicinename(medicationRequestBean.getMedicinename())
                .dosage(medicationRequestBean.getDosage())
                .frequency(medicationRequestBean.getFrequency())
                .startDate(medicationRequestBean.getStartDate())
                .endDate(medicationRequestBean.getEndDate())
                .notes(medicationRequestBean.getNotes())
                .build();
    }
    
    public MedicationResponseBean toBean(Medication medication) {
        return MedicationResponseBean.builder()
                .medicationid(medication.getId())
                .userId(medication.getUserId())
                .medicinename(medication.getMedicinename())
                .dosage(medication.getDosage())
                .frequency(medication.getFrequency())
                .startDate(medication.getStartDate())
                .endDate(medication.getEndDate())
                .notes(medication.getNotes())
                .build();
    }

    // List conversions
    public List<HealthRecordResponseBean> toHealthRecordBeans(List<HealthRecord> healthRecords) {
        return healthRecords.stream()
                .map(this::toBean)
                .collect(Collectors.toList());
    }
    
    public List<PeriodRecordResponseBean> toPeriodRecordBeans(List<PeriodRecord> periodRecords) {
        return periodRecords.stream()
                .map(this::toBean)
                .collect(Collectors.toList());
    }
    
    public List<ReminderResponseBean> toReminderBeans(List<Reminder> reminders) {
        return reminders.stream()
                .map(this::toBean)
                .collect(Collectors.toList());
    }
    
    public List<MedicationResponseBean> toMedicationBeans(List<Medication> medications) {
        return medications.stream()
                .map(this::toBean)
                .collect(Collectors.toList());
    }
} 