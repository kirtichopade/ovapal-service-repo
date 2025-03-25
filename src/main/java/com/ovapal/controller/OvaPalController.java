package com.ovapal.controller;

import com.ovapal.bean.*;
import com.ovapal.service.OvaPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ovapal")
public class OvaPalController {
    
    @Autowired
    private OvaPalService ovaPalService;

    @PostMapping("/users")
    public ResponseEntity<UserResponseBean> createUser(@RequestBody UserRequestBean userRequestBean) {
        return ResponseEntity.ok(ovaPalService.createUser(userRequestBean));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseBean> login(@RequestBody LoginRequestBean loginRequestBean) {
        return ResponseEntity.ok(ovaPalService.loginUser(loginRequestBean));
    }

    @PostMapping("/health")
    public ResponseEntity<HealthRecordResponseBean> createHealthRecord(
            @RequestBody HealthRecordRequestBean healthRecordRequestBean) {
        return ResponseEntity.ok(ovaPalService.saveHealthRecord(healthRecordRequestBean));
    }

    @GetMapping("/health/{userId}")
    public ResponseEntity<List<HealthRecordResponseBean>> getHealthRecords(@PathVariable Long userId) {
        return ResponseEntity.ok(ovaPalService.getHealthRecords(userId));
    }

    // PUT - Update existing health record
    @PutMapping("/health/{healthId}")
    public ResponseEntity<HealthRecordResponseBean> updateHealthRecord(
            @PathVariable Long healthId,
            @RequestBody HealthRecordRequestBean healthRecordRequestBean) {
        return ResponseEntity.ok(ovaPalService.updateHealthRecord(healthId, healthRecordRequestBean));
    }

    @GetMapping("/period/{userId}")
    public ResponseEntity<List<PeriodRecordResponseBean>> getPeriodRecords(@PathVariable Long userId) {
        return ResponseEntity.ok(ovaPalService.getPeriodRecords(userId));
    }

    @PostMapping("/period")
    public ResponseEntity<PeriodRecordResponseBean> savePeriodRecord(@RequestBody PeriodRecordRequestBean periodRecordRequestBean) {
        return ResponseEntity.ok(ovaPalService.savePeriodRecord(periodRecordRequestBean));
    }

    @PutMapping("/period/{periodRecId}")
    public ResponseEntity<PeriodRecordResponseBean> updatePeriodRecord(
            @PathVariable Long periodRecId,
            @RequestBody PeriodRecordRequestBean periodRecordRequestBean) {
        return ResponseEntity.ok(ovaPalService.updatePeriodRecord(periodRecId, periodRecordRequestBean));
    }

    @GetMapping("/reminders/{userId}")
    public ResponseEntity<List<ReminderResponseBean>> getReminders(@PathVariable Long userId) {
        return ResponseEntity.ok(ovaPalService.getReminders(userId));
    }

    @PostMapping("/reminders")
    public ResponseEntity<ReminderResponseBean> setReminder(@RequestBody ReminderRequestBean reminderRequestBean) {
        return ResponseEntity.ok(ovaPalService.setReminder(reminderRequestBean));
    }

    @PostMapping("/medications")
    public ResponseEntity<MedicationResponseBean> addMedication(@RequestBody MedicationRequestBean medicationRequestBean) {
        return ResponseEntity.ok(ovaPalService.addMedication(medicationRequestBean));
    }

    @GetMapping("/medications/{userId}")
    public ResponseEntity<List<MedicationResponseBean>> getMedications(@PathVariable Long userId) {
        return ResponseEntity.ok(ovaPalService.getMedications(userId));
    }
} 