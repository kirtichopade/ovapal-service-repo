package com.ovapal.controller;

import com.ovapal.bean.*;
import com.ovapal.service.OvaPalService;
import com.ovapal.util.JwtTokenUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/ovapal")
@CrossOrigin(origins = "http://localhost:3000")
public class OvaPalController {
    private static final Logger logger = LoggerFactory.getLogger(OvaPalController.class);

    @Autowired
    private OvaPalService ovaPalService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    // Public endpoints (no token required)
    @PostMapping("/users")
    public ResponseEntity<UserResponseBean> createUser(@RequestBody UserRequestBean userRequestBean) {
        return ResponseEntity.ok(ovaPalService.createUser(userRequestBean));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseBean> login(@RequestBody LoginRequestBean loginRequestBean) {
        LoginResponseBean userLogin = ovaPalService.loginUser(loginRequestBean);

        if (userLogin == null || userLogin.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Generate JWT token
        String token = jwtTokenUtil.generateToken(userLogin.getUser().getUserId());

        // Create response with token and user details
        LoginResponseBean response = new LoginResponseBean();
        response.setToken(token);
        response.setUser(userLogin.getUser());

        return ResponseEntity.ok(response);
    }

    // Health Record Endpoints
    @PostMapping("/health")
    public ResponseEntity<?> createHealthRecord(
            @RequestBody HealthRecordRequestBean healthRecordRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.saveHealthRecord(healthRecordRequestBean));
    }

    @GetMapping("/health/{userId}")
    public ResponseEntity<?> getHealthRecords(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.getHealthRecords(userId));
    }

    @PutMapping("/health/{healthId}")
    public ResponseEntity<?> updateHealthRecord(
            @PathVariable Long healthId,
            @RequestBody HealthRecordRequestBean healthRecordRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.updateHealthRecord(healthId, healthRecordRequestBean));
    }

    // Period Record Endpoints
    @GetMapping("/period/{userId}")
    public ResponseEntity<?> getPeriodRecords(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.getPeriodRecords(userId));
    }

    @PostMapping("/period")
    public ResponseEntity<?> savePeriodRecord(
            @RequestBody PeriodRecordRequestBean periodRecordRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.savePeriodRecord(periodRecordRequestBean));
    }

    @PutMapping("/period/{periodRecId}")
    public ResponseEntity<?> updatePeriodRecord(
            @PathVariable Long periodRecId,
            @RequestBody PeriodRecordRequestBean periodRecordRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.updatePeriodRecord(periodRecId, periodRecordRequestBean));
    }

    // Reminder Endpoints
    @GetMapping("/reminders/{userId}")
    public ResponseEntity<?> getReminders(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.getReminders(userId));
    }

    @PostMapping("/reminders")
    public ResponseEntity<?> setReminder(
            @RequestBody ReminderRequestBean reminderRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.setReminder(reminderRequestBean));
    }

    @PutMapping("/reminders/{reminderId}")
    public ResponseEntity<?> updateReminder(
            @PathVariable Long reminderId,
            @RequestBody ReminderRequestBean reminderRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.updateReminder(reminderId, reminderRequestBean));
    }

    @DeleteMapping("/reminders/{reminderId}")
    public ResponseEntity<?> deleteReminder(
            @PathVariable Long reminderId,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ovaPalService.deleteReminder(reminderId);
        return ResponseEntity.ok("Reminder deleted successfully");
    }

    // Medication Endpoints
    @PostMapping("/medications")
    public ResponseEntity<?> addMedication(
            @RequestBody MedicationRequestBean medicationRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.addMedication(medicationRequestBean));
    }

    @PutMapping("/medications/{medicationId}")
    public ResponseEntity<?> updateMedication(
            @PathVariable Long medicationId,
            @RequestBody MedicationRequestBean medicationRequestBean,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.updateMedication(medicationId, medicationRequestBean));
    }

    @GetMapping("/medications/{userId}")
    public ResponseEntity<?> getMedications(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ovaPalService.getMedications(userId));
    }

    @DeleteMapping("/medications/{medicationId}")
    public ResponseEntity<?> deleteMedication(
            @PathVariable Long medicationId,
            @RequestHeader("Authorization") String authHeader) {
        if (!validateToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ovaPalService.deleteMedication(medicationId);
        return ResponseEntity.ok("Medication deleted successfully");
    }

    private boolean validateToken(String authHeader) {
        try {
            if (authHeader == null || authHeader.isBlank()) {
                logger.warn("Authorization header is missing");
                return false;
            }

            String token = authHeader.trim();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim(); // Remove "Bearer " prefix
            }

            if (token.isEmpty()) {
                logger.warn("Empty token after extraction");
                return false;
            }

            boolean isValid = jwtTokenUtil.validateToken(token);
            if (!isValid) {
                logger.warn("Token validation failed");
            } else {
                logger.debug("Token validated successfully");
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
    }
