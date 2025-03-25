package com.ovapal.service;

import com.ovapal.bean.*;
import com.ovapal.bean.request.UserRequestBean;
import com.ovapal.entity.*;
import com.ovapal.repository.*;
import com.ovapal.exception.ResourceNotFoundException;
import com.ovapal.exception.InvalidOperationException;
import com.ovapal.exception.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OvaPalService {
    private static final Logger logger = LoggerFactory.getLogger(OvaPalService.class);
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final int MIN_PASSWORD_LENGTH = 8;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    
    @Autowired
    private PeriodRecordRepository periodRecordRepository;
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    @Autowired
    private MedicationRepository medicationRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void init() {
        logger.info("OvaPalService initialized successfully");
    }

    // User Management
    @Transactional
    public UserResponseBean createUser(UserRequestBean userRequestBean) {
        logger.info("Creating user with email: {}", userRequestBean.getEmail());
        
        // Validate email
        if (userRequestBean.getEmail() == null || !EMAIL_PATTERN.matcher(userRequestBean.getEmail()).matches()) {
            logger.error("Invalid email format: {}", userRequestBean.getEmail());
            throw new InvalidOperationException("Invalid email format");
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(userRequestBean.getEmail()).isPresent()) {
            logger.error("Email already exists: {}", userRequestBean.getEmail());
            throw new InvalidOperationException("Email is already registered");
        }
        
        // Validate password
        if (userRequestBean.getPassword() == null || userRequestBean.getPassword().length() < MIN_PASSWORD_LENGTH) {
            logger.error("Password does not meet minimum requirements");
            throw new InvalidOperationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        
        // Validate name
        if (userRequestBean.getName() == null || userRequestBean.getName().trim().isEmpty()) {
            logger.error("Name is required");
            throw new InvalidOperationException("Name is required");
        }
        
        // Map bean to entity
        User user = User.builder()
                .name(userRequestBean.getName())
                .email(userRequestBean.getEmail())
                .password(passwordEncoder.encode(userRequestBean.getPassword()))
                .age(userRequestBean.getAge())
                .build();
        
        // Save the user
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getUserid());
        
        // Map entity to response bean
        return mapUserToResponseBean(savedUser);
    }
    
    public UserResponseBean loginUser(LoginRequestBean loginRequestBean) {
        logger.info("Login attempt for user with email: {}", loginRequestBean.getEmail());
        
        // Validate login request
        if (loginRequestBean.getEmail() == null || loginRequestBean.getPassword() == null) {
            logger.error("Email or password is null");
            throw new AuthenticationException("Email and password are required");
        }
        
        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(loginRequestBean.getEmail());
        
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequestBean.getPassword(), userOpt.get().getPassword())) {
            logger.info("Login successful for user: {}", userOpt.get().getName());
            return mapUserToResponseBean(userOpt.get());
        }
        
        logger.warn("Login failed for email: {}", loginRequestBean.getEmail());
        throw new AuthenticationException("Invalid email or password");
    }
    
    // Health Records
    public List<HealthRecordResponseBean> getHealthRecords(Long userId) {
        logger.info("Fetching health records for user ID: {}", userId);
        
        // Verify user exists
        verifyUserExists(userId);
        
        List<HealthRecord> healthRecords = healthRecordRepository.findByUserId(userId);
        logger.info("Found {} health records for user ID: {}", healthRecords.size(), userId);
        
        // Map entities to response beans
        return healthRecords.stream()
                .map(this::mapHealthRecordToResponseBean)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public HealthRecordResponseBean saveHealthRecord(HealthRecordRequestBean healthRecordRequestBean) {
        logger.info("Saving health record for user ID: {}", healthRecordRequestBean.getUserId());
        
        // Verify user exists
        verifyUserExists(healthRecordRequestBean.getUserId());
        
        // Map bean to entity
        HealthRecord healthRecord = HealthRecord.builder()
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
        
        // Validate health record
        validateHealthRecord(healthRecord);
        
        // Set record date to today if not provided
        if (healthRecord.getRecordDate() == null) {
            healthRecord.setRecordDate(LocalDate.now());
            logger.debug("Setting record date to today: {}", healthRecord.getRecordDate());
        }
        
        HealthRecord savedRecord = healthRecordRepository.save(healthRecord);
        logger.info("Health record saved with ID: {}", savedRecord.getHealthId());
        
        // Map entity to response bean
        return mapHealthRecordToResponseBean(savedRecord);
    }
    
    // Period Records
    public List<PeriodRecordResponseBean> getPeriodRecords(Long userId) { 
        logger.info("Fetching period records for user ID: {}", userId);
        
        // Verify user exists
        verifyUserExists(userId);
        
        List<PeriodRecord> periodRecords = periodRecordRepository.findByUserId(userId);
        logger.info("Found {} period records for user ID: {}", periodRecords.size(), userId);
        
        // Map entities to response beans
        return periodRecords.stream()
                .map(this::mapPeriodRecordToResponseBean)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PeriodRecordResponseBean savePeriodRecord(PeriodRecordRequestBean periodRecordRequestBean) {
        logger.info("Saving period record for user ID: {}", periodRecordRequestBean.getUserId());
        
        // Verify user exists
        verifyUserExists(periodRecordRequestBean.getUserId());
        
        // Map bean to entity
        PeriodRecord periodRecord = PeriodRecord.builder()
                .userId(periodRecordRequestBean.getUserId())
                .startDate(periodRecordRequestBean.getStartDate())
                .endDate(periodRecordRequestBean.getEndDate())
                .flow(periodRecordRequestBean.getFlow())
                .symptoms(periodRecordRequestBean.getSymptoms())
                .mood(periodRecordRequestBean.getMood())
                .notes(periodRecordRequestBean.getNotes())
                .build();
        
        // Validate period record
        validatePeriodRecord(periodRecord);
        
        PeriodRecord savedRecord = periodRecordRepository.save(periodRecord);
        logger.info("Period record saved with ID: {}", savedRecord.getPeriodrecid());
        
        // Map entity to response bean
        return mapPeriodRecordToResponseBean(savedRecord);
    }
    
    @Transactional
    public PeriodRecordResponseBean updatePeriodRecord(Long periodRecId, PeriodRecordRequestBean periodRecordRequestBean) {
        logger.info("Updating period record ID: {} for user ID: {}", periodRecId, periodRecordRequestBean.getUserId());
        
        // Verify user exists
        verifyUserExists(periodRecordRequestBean.getUserId());
        
        // Verify record exists and belongs to the user
        PeriodRecord existingRecord = periodRecordRepository.findById(periodRecId)
                .orElseThrow(() -> new ResourceNotFoundException("Period record not found with ID: " + periodRecId));
        
        if (!existingRecord.getUserId().equals(periodRecordRequestBean.getUserId())) {
            logger.error("User ID mismatch for period record ID: {}", periodRecId);
            throw new InvalidOperationException("Period record does not belong to this user");
        }
        
        // Map bean to entity
        PeriodRecord periodRecord = PeriodRecord.builder()
                .periodrecid(periodRecId) // Set the ID to ensure we're updating not creating
                .userId(periodRecordRequestBean.getUserId())
                .startDate(periodRecordRequestBean.getStartDate())
                .endDate(periodRecordRequestBean.getEndDate())
                .flow(periodRecordRequestBean.getFlow())
                .symptoms(periodRecordRequestBean.getSymptoms())
                .mood(periodRecordRequestBean.getMood())
                .notes(periodRecordRequestBean.getNotes())
                .build();
        
        // Validate period record
        validatePeriodRecord(periodRecord);
        
        PeriodRecord updatedRecord = periodRecordRepository.save(periodRecord);
        logger.info("Period record updated with ID: {}", updatedRecord.getPeriodrecid());
        
        // Map entity to response bean
        return mapPeriodRecordToResponseBean(updatedRecord);
    }
    
    // Reminders
    public List<ReminderResponseBean> getReminders(Long userId) {
        logger.info("Fetching reminders for user ID: {}", userId);
        
        // Verify user exists
        verifyUserExists(userId);
        
        List<Reminder> reminders = reminderRepository.findByUserId(userId);
        
        // Filter active reminders
        List<Reminder> activeReminders = reminders.stream()
                .filter(reminder -> reminder.getIsActive() == null || reminder.getIsActive())
                .collect(Collectors.toList());
        
        logger.info("Found {} active reminders for user ID: {}", activeReminders.size(), userId);
        
        // Map entities to response beans
        return activeReminders.stream()
                .map(this::mapReminderToResponseBean)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReminderResponseBean setReminder(ReminderRequestBean reminderRequestBean) {
        logger.info("Setting reminder for user ID: {}", reminderRequestBean.getUserId());
        
        // Verify user exists
        verifyUserExists(reminderRequestBean.getUserId());
        
        // Map bean to entity
        Reminder reminder = Reminder.builder()
                .userId(reminderRequestBean.getUserId())
                .title(reminderRequestBean.getTitle())
                .description(reminderRequestBean.getDescription())
                .reminderDate(reminderRequestBean.getReminderDate())
                .reminderTime(reminderRequestBean.getReminderTime())
                .isRepeating(reminderRequestBean.getIsRepeating())
                .repeatFrequency(reminderRequestBean.getRepeatFrequency())
                .isActive(reminderRequestBean.getIsActive())
                .build();
        
        // Validate reminder
        validateReminder(reminder);
        
        // Set isActive to true by default if not specified
        if (reminder.getIsActive() == null) {
            reminder.setIsActive(true);
        }
        
        Reminder savedReminder = reminderRepository.save(reminder);
        logger.info("Reminder saved with ID: {}", savedReminder.getReminderid());
        
        // Map entity to response bean
        return mapReminderToResponseBean(savedReminder);
    }
    
    // Medications
    @Transactional
    public MedicationResponseBean addMedication(MedicationRequestBean medicationRequestBean) {
        logger.info("Adding medication for user ID: {}", medicationRequestBean.getUserId());
        
        // Verify user exists
        verifyUserExists(medicationRequestBean.getUserId());
        
        // Map bean to entity
        Medication medication = Medication.builder()
                .userId(medicationRequestBean.getUserId())
                .medicine(medicationRequestBean.getMedicine())
                .dosage(medicationRequestBean.getDosage())
                .frequency(medicationRequestBean.getFrequency())
                .startDate(medicationRequestBean.getStartDate())
                .endDate(medicationRequestBean.getEndDate())
                .notes(medicationRequestBean.getNotes())
                .build();
        
        // Validate medication
        validateMedication(medication);
        
        Medication savedMedication = medicationRepository.save(medication);
        logger.info("Medication saved with ID: {}", savedMedication.getMedicineid());
        
        // Map entity to response bean
        return mapMedicationToResponseBean(savedMedication);
    }
    
    public List<MedicationResponseBean> getMedications(Long userId) {
        logger.info("Fetching medications for user ID: {}", userId);
        
        // Verify user exists
        verifyUserExists(userId);
        
        List<Medication> medications = medicationRepository.findByUserId(userId);
        
        // Filter current medications (end date is null or in the future)
        LocalDate now = LocalDate.now();
        List<Medication> currentMedications = medications.stream()
                .filter(med -> med.getEndDate() == null || !med.getEndDate().isBefore(now))
                .collect(Collectors.toList());
        
        logger.info("Found {} current medications for user ID: {}", currentMedications.size(), userId);
        
        // Map entities to response beans
        return currentMedications.stream()
                .map(this::mapMedicationToResponseBean)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    private void verifyUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            logger.error("User not found with ID: {}", userId);
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }
    
    private void validateHealthRecord(HealthRecord healthRecord) {
        if (healthRecord.getWeight() != null && healthRecord.getWeight() <= 0) {
            throw new InvalidOperationException("Weight must be a positive value");
        }
        
        if (healthRecord.getHeight() != null && healthRecord.getHeight() <= 0) {
            throw new InvalidOperationException("Height must be a positive value");
        }
        
        if (healthRecord.getHeartRate() != null && (healthRecord.getHeartRate() < 20 || healthRecord.getHeartRate() > 220)) {
            throw new InvalidOperationException("Heart rate must be between 20 and 220 bpm");
        }
        
        if (healthRecord.getBloodPressureSystolic() != null && healthRecord.getBloodPressureDiastolic() != null) {
            if (healthRecord.getBloodPressureSystolic() <= healthRecord.getBloodPressureDiastolic()) {
                throw new InvalidOperationException("Systolic pressure must be greater than diastolic pressure");
            }
        }
    }
    
    private void validatePeriodRecord(PeriodRecord periodRecord) {
        if (periodRecord.getStartDate() == null) {
            throw new InvalidOperationException("Start date is required");
        }
        
        if (periodRecord.getEndDate() != null && periodRecord.getEndDate().isBefore(periodRecord.getStartDate())) {
            throw new InvalidOperationException("End date cannot be before start date");
        }
        
        if (periodRecord.getEndDate() != null) {
            long daysBetween = ChronoUnit.DAYS.between(periodRecord.getStartDate(), periodRecord.getEndDate());
            if (daysBetween > 14) {
                logger.warn("Period duration of {} days is unusually long for user ID: {}", 
                        daysBetween, periodRecord.getUserId());
            }
        }
    }
    
    private void validateReminder(Reminder reminder) {
        if (reminder.getTitle() == null || reminder.getTitle().trim().isEmpty()) {
            throw new InvalidOperationException("Reminder title is required");
        }
        
        if (reminder.getReminderDate() == null) {
            throw new InvalidOperationException("Reminder date is required");
        }
        
        if (reminder.getReminderTime() == null) {
            throw new InvalidOperationException("Reminder time is required");
        }
        
        // Check if the reminder is set for a past date/time
        LocalDate today = LocalDate.now();
        if (reminder.getReminderDate().isBefore(today)) {
            throw new InvalidOperationException("Cannot set reminder for a past date");
        }
    }
    
    private void validateMedication(Medication medication) {
        if (medication.getMedicine() == null || medication.getMedicine().trim().isEmpty()) {
            throw new InvalidOperationException("Medication name is required");
        }
        
        if (medication.getDosage() == null || medication.getDosage().trim().isEmpty()) {
            throw new InvalidOperationException("Dosage is required");
        }
        
        if (medication.getFrequency() == null || medication.getFrequency().trim().isEmpty()) {
            throw new InvalidOperationException("Frequency is required");
        }
        
        if (medication.getStartDate() == null) {
            throw new InvalidOperationException("Start date is required");
        }
        
        if (medication.getEndDate() != null && medication.getEndDate().isBefore(medication.getStartDate())) {
            throw new InvalidOperationException("End date cannot be before start date");
        }
    }
    
    // Mapper methods
    private UserResponseBean mapUserToResponseBean(User user) {
        return UserResponseBean.builder()
                .userId(user.getUserid())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();
    }
    
    private HealthRecordResponseBean mapHealthRecordToResponseBean(HealthRecord healthRecord) {
        return HealthRecordResponseBean.builder()
                .healthId(healthRecord.getHealthId())
                .userId(healthRecord.getUserId())
                .recordDate(LocalDate.now())
                .weight(healthRecord.getWeight())
                .height(healthRecord.getHeight())
                .temperature(healthRecord.getTemperature())
                .heartRate(healthRecord.getHeartRate())
                .bloodPressureSystolic(healthRecord.getBloodPressureSystolic())
                .bloodPressureDiastolic(healthRecord.getBloodPressureDiastolic())
                .notes(healthRecord.getNotes())
                .build();
    }
    
    private PeriodRecordResponseBean mapPeriodRecordToResponseBean(PeriodRecord periodRecord) {
        return PeriodRecordResponseBean.builder()
                .periodRecId(periodRecord.getPeriodrecid())
                .userId(periodRecord.getUserId())
                .startDate(periodRecord.getStartDate())
                .endDate(periodRecord.getEndDate())
                .flow(periodRecord.getFlow())
                .symptoms(periodRecord.getSymptoms())
                .mood(periodRecord.getMood())
                .notes(periodRecord.getNotes())
                .build();
    }
    
    private ReminderResponseBean mapReminderToResponseBean(Reminder reminder) {
        return ReminderResponseBean.builder()
                .reminderId(reminder.getReminderid())
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
    
    private MedicationResponseBean mapMedicationToResponseBean(Medication medication) {
        return MedicationResponseBean.builder()
                .medicationId(medication.getMedicineid())
                .userId(medication.getUserId())
                .medicine(medication.getMedicine())
                .dosage(medication.getDosage())
                .frequency(medication.getFrequency())
                .startDate(medication.getStartDate())
                .endDate(medication.getEndDate())
                .notes(medication.getNotes())
                .build();
    }
} 