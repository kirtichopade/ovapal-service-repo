package com.ovapal.service;

import com.ovapal.bean.*;
import com.ovapal.entity.*;
import com.ovapal.exception.AuthenticationException;
import com.ovapal.exception.InvalidOperationException;
import com.ovapal.exception.ResourceNotFoundException;
import com.ovapal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OvaPalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HealthRecordRepository healthRecordRepository;

    @Mock
    private PeriodRecordRepository periodRecordRepository;

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private OvaPalService ovaPalService;

    private User testUser;
    private UserRequestBean userRequestBean;
    private LoginRequestBean loginRequestBean;
    private HealthRecord testHealthRecord;
    private HealthRecordRequestBean healthRecordRequestBean;
    private PeriodRecord testPeriodRecord;
    private PeriodRecordRequestBean periodRecordRequestBean;
    private Reminder testReminder;
    private ReminderRequestBean reminderRequestBean;
    private Medication testMedication;
    private MedicationRequestBean medicationRequestBean;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = User.builder()
                .userid(1L)
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword")
                .age(25)
                .build();

        userRequestBean = UserRequestBean.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .age(25)
                .build();

        loginRequestBean = LoginRequestBean.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // Set up test health record
        testHealthRecord = HealthRecord.builder()
                .healthId(1L)
                .userId(1L)
                .recordDate(LocalDate.now())
                .weight(60.5)
                .height(170.0)
                .temperature(36.5)
                .heartRate(75)
                .bloodPressureSystolic(120)
                .bloodPressureDiastolic(80)
                .notes("Normal health")
                .build();

        healthRecordRequestBean = HealthRecordRequestBean.builder()
                .userId(1L)
                .recordDate(LocalDate.now())
                .weight(60.5)
                .height(170.0)
                .temperature(36.5)
                .heartRate(75)
                .bloodPressureSystolic(120)
                .bloodPressureDiastolic(80)
                .notes("Normal health")
                .build();

        // Set up test period record
        testPeriodRecord = PeriodRecord.builder()
                .periodrecid(1L)
                .userId(1L)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now())
                .flow("Medium")
                .symptoms("Cramps")
                .mood("Normal")
                .notes("Regular cycle")
                .build();

        periodRecordRequestBean = PeriodRecordRequestBean.builder()
                .userId(1L)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now())
                .flow("Medium")
                .symptoms("Cramps")
                .mood("Normal")
                .notes("Regular cycle")
                .build();

        // Set up test reminder
        testReminder = Reminder.builder()
                .reminderid(1L)
                .userId(1L)
                .title("Medication Reminder")
                .description("Take pain medication")
                .reminderDate(LocalDate.now().plusDays(1))
                .reminderTime(LocalTime.of(8, 0))
                .isRepeating(true)
                .repeatFrequency("Daily")
                .isActive(true)
                .build();

        reminderRequestBean = ReminderRequestBean.builder()
                .userId(1L)
                .title("Medication Reminder")
                .description("Take pain medication")
                .reminderDate(LocalDate.now().plusDays(1))
                .reminderTime(LocalTime.of(8, 0))
                .isRepeating(true)
                .repeatFrequency("Daily")
                .isActive(true)
                .build();

        // Set up test medication
        testMedication = Medication.builder()
                .medicineid(1L)
                .userId(1L)
                .medicine("Ibuprofen")
                .dosage("200mg")
                .frequency("Twice daily")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .notes("Take with food")
                .build();

        medicationRequestBean = MedicationRequestBean.builder()
                .userId(1L)
                .medicine("Ibuprofen")
                .dosage("200mg")
                .frequency("Twice daily")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .notes("Take with food")
                .build();
    }

    // User Management Tests
    @Test
    void createUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseBean result = ovaPalService.createUser(userRequestBean);

        assertNotNull(result);
        assertEquals(testUser.getUserid(), result.getUserId());
        verify(userRepository).findByEmail(userRequestBean.getEmail());
        verify(passwordEncoder).encode(userRequestBean.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_InvalidEmail() {
        UserRequestBean invalidEmailRequest = UserRequestBean.builder()
                .name("Test User")
                .email("invalid-email")
                .password("password123")
                .age(25)
                .build();

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ovaPalService.createUser(invalidEmailRequest)
        );

        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        UserResponseBean result = ovaPalService.loginUser(loginRequestBean);

        assertNotNull(result);
        assertEquals(testUser.getUserid(), result.getUserId());
        verify(userRepository).findByEmail(loginRequestBean.getEmail());
        verify(passwordEncoder).matches(loginRequestBean.getPassword(), testUser.getPassword());
    }

    @Test
    void loginUser_InvalidCredentials() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> ovaPalService.loginUser(loginRequestBean)
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }

    // Health Record Tests
    @Test
    void getHealthRecords_Success() {
        List<HealthRecord> healthRecords = Arrays.asList(testHealthRecord);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(healthRecordRepository.findByUserId(anyLong())).thenReturn(healthRecords);

        List<HealthRecordResponseBean> result = ovaPalService.getHealthRecords(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
        verify(healthRecordRepository).findByUserId(1L);
    }

    @Test
    void getHealthRecords_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ovaPalService.getHealthRecords(999L)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(healthRecordRepository, never()).findByUserId(anyLong());
    }

    @Test
    void saveHealthRecord_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(healthRecordRepository.save(any(HealthRecord.class))).thenReturn(testHealthRecord);

        HealthRecordResponseBean result = ovaPalService.saveHealthRecord(healthRecordRequestBean);

        assertNotNull(result);
        assertEquals(testHealthRecord.getHealthId(), result.getHealthId());
        verify(userRepository).existsById(healthRecordRequestBean.getUserId());
        verify(healthRecordRepository).save(any(HealthRecord.class));
    }

    // Period Record Tests
    @Test
    void getPeriodRecords_Success() {
        List<PeriodRecord> periodRecords = Arrays.asList(testPeriodRecord);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(periodRecordRepository.findByUserId(anyLong())).thenReturn(periodRecords);

        List<PeriodRecordResponseBean> result = ovaPalService.getPeriodRecords(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
        verify(periodRecordRepository).findByUserId(1L);
    }

    @Test
    void savePeriodRecord_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(periodRecordRepository.save(any(PeriodRecord.class))).thenReturn(testPeriodRecord);

        PeriodRecordResponseBean result = ovaPalService.savePeriodRecord(periodRecordRequestBean);

        assertNotNull(result);
        assertEquals(testPeriodRecord.getPeriodrecid(), result.getPeriodRecId());
        verify(userRepository).existsById(periodRecordRequestBean.getUserId());
        verify(periodRecordRepository).save(any(PeriodRecord.class));
    }

    @Test
    void updatePeriodRecord_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(periodRecordRepository.findById(anyLong())).thenReturn(Optional.of(testPeriodRecord));
        when(periodRecordRepository.save(any(PeriodRecord.class))).thenReturn(testPeriodRecord);

        PeriodRecordResponseBean result = ovaPalService.updatePeriodRecord(1L, periodRecordRequestBean);

        assertNotNull(result);
        assertEquals(testPeriodRecord.getPeriodrecid(), result.getPeriodRecId());
        verify(periodRecordRepository).findById(1L);
        verify(periodRecordRepository).save(any(PeriodRecord.class));
    }

    @Test
    void updatePeriodRecord_NotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(periodRecordRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ovaPalService.updatePeriodRecord(999L, periodRecordRequestBean)
        );

        assertTrue(exception.getMessage().contains("Period record not found"));
        verify(periodRecordRepository, never()).save(any(PeriodRecord.class));
    }

    // Reminder Tests
    @Test
    void getReminders_Success() {
        List<Reminder> reminders = Arrays.asList(testReminder);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(reminderRepository.findByUserId(anyLong())).thenReturn(reminders);

        List<ReminderResponseBean> result = ovaPalService.getReminders(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
        verify(reminderRepository).findByUserId(1L);
    }

    @Test
    void setReminder_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        ReminderResponseBean result = ovaPalService.setReminder(reminderRequestBean);

        assertNotNull(result);
        assertEquals(testReminder.getReminderid(), result.getReminderId());
        verify(userRepository).existsById(reminderRequestBean.getUserId());
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void updateReminder_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(reminderRepository.findById(anyLong())).thenReturn(Optional.of(testReminder));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        ReminderResponseBean result = ovaPalService.updateReminder(1L, reminderRequestBean);

        assertNotNull(result);
        assertEquals(testReminder.getReminderid(), result.getReminderId());
        verify(reminderRepository).findById(1L);
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void updateReminder_NotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(reminderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ovaPalService.updateReminder(999L, reminderRequestBean)
        );

        assertTrue(exception.getMessage().contains("Reminder not found"));
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    void deleteReminder_Success() {
        when(reminderRepository.existsById(anyLong())).thenReturn(true);
        when(reminderRepository.findById(anyLong())).thenReturn(Optional.of(testReminder));

        ovaPalService.deleteReminder(1L);

        verify(reminderRepository).findById(1L);
        verify(reminderRepository).save(testReminder);
        assertFalse(testReminder.getIsActive());
    }

    @Test
    void deleteReminder_NotFound() {
        when(reminderRepository.existsById(anyLong())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ovaPalService.deleteReminder(999L)
        );

        assertTrue(exception.getMessage().contains("Reminder not found"));
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    // Medication Tests
    @Test
    void getMedications_Success() {
        List<Medication> medications = Arrays.asList(testMedication);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(medicationRepository.findByUserId(anyLong())).thenReturn(medications);

        List<MedicationResponseBean> result = ovaPalService.getMedications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).existsById(1L);
        verify(medicationRepository).findByUserId(1L);
    }

    @Test
    void addMedication_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(medicationRepository.save(any(Medication.class))).thenReturn(testMedication);

        MedicationResponseBean result = ovaPalService.addMedication(medicationRequestBean);

        assertNotNull(result);
        assertEquals(testMedication.getMedicineid(), result.getMedicationId());
        verify(userRepository).existsById(medicationRequestBean.getUserId());
        verify(medicationRepository).save(any(Medication.class));
    }

    @Test
    void updateMedication_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(medicationRepository.findById(anyLong())).thenReturn(Optional.of(testMedication));
        when(medicationRepository.save(any(Medication.class))).thenReturn(testMedication);

        MedicationResponseBean result = ovaPalService.updateMedication(1L, medicationRequestBean);

        assertNotNull(result);
        assertEquals(testMedication.getMedicineid(), result.getMedicationId());
        verify(medicationRepository).findById(1L);
        verify(medicationRepository).save(any(Medication.class));
    }

    @Test
    void updateMedication_NotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(medicationRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ovaPalService.updateMedication(999L, medicationRequestBean)
        );

        assertTrue(exception.getMessage().contains("Medication not found"));
        verify(medicationRepository, never()).save(any(Medication.class));
    }

    @Test
    void deleteMedication_Success() {
        when(medicationRepository.existsById(anyLong())).thenReturn(true);

        ovaPalService.deleteMedication(1L);

        verify(medicationRepository).deleteById(1L);
    }

    @Test
    void deleteMedication_NotFound() {
        when(medicationRepository.existsById(anyLong())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ovaPalService.deleteMedication(999L)
        );

        assertTrue(exception.getMessage().contains("Medication not found"));
        verify(medicationRepository, never()).deleteById(anyLong());
    }

    // Validation Tests
    @Test
    void validateHealthRecord_InvalidWeight() {
        HealthRecord invalidRecord = HealthRecord.builder()
                .weight(-10.0)
                .build();

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ovaPalService.validateHealthRecord(invalidRecord)
        );

        assertEquals("Weight must be a positive value", exception.getMessage());
    }

    @Test
    void validatePeriodRecord_InvalidDates() {
        PeriodRecord invalidRecord = PeriodRecord.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().minusDays(1))
                .build();

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ovaPalService.validatePeriodRecord(invalidRecord)
        );

        assertEquals("End date cannot be before start date", exception.getMessage());
    }
}