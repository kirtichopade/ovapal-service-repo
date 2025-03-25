package com.ovapal.service;

import com.ovapal.bean.*;
import com.ovapal.bean.request.UserRequestBean;
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

import java.sql.Timestamp;
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
                .id(1L)
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
                .id(1L)
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
                .id(1L)
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
                .id(1L)
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
                .id(1L)
                .userId(1L)
                .medicinename("Ibuprofen")
                .dosage("200mg")
                .frequency("Twice daily")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .notes("Take with food")
                .build();

        medicationRequestBean = MedicationRequestBean.builder()
                .userId(1L)
                .medicinename("Ibuprofen")
                .dosage("200mg")
                .frequency("Twice daily")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .notes("Take with food")
                .build();
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserResponseBean result = ovaPalService.createUser(userRequestBean);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        
        verify(userRepository).findByEmail(userRequestBean.getEmail());
        verify(passwordEncoder).encode(userRequestBean.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_InvalidEmail() {
        // Arrange
        UserRequestBean invalidEmailRequest = UserRequestBean.builder()
                .name("Test User")
                .email("invalid-email")
                .password("password123")
                .age(25)
                .build();

        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> ovaPalService.createUser(invalidEmailRequest)
        );
        
        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        UserResponseBean result = ovaPalService.loginUser(loginRequestBean);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        
        verify(userRepository).findByEmail(loginRequestBean.getEmail());
        verify(passwordEncoder).matches(loginRequestBean.getPassword(), testUser.getPassword());
    }

    @Test
    void loginUser_InvalidCredentials() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> ovaPalService.loginUser(loginRequestBean)
        );
        
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void getHealthRecords_Success() {
        // Arrange
        List<HealthRecord> healthRecords = Arrays.asList(testHealthRecord);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(healthRecordRepository.findByUserId(anyLong())).thenReturn(healthRecords);

        // Act
        List<HealthRecordResponseBean> result = ovaPalService.getHealthRecords(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHealthRecord.getId(), result.get(0).getId());
        assertEquals(testHealthRecord.getUserId(), result.get(0).getUserId());
        assertEquals(testHealthRecord.getWeight(), result.get(0).getWeight());
        
        verify(userRepository).existsById(1L);
        verify(healthRecordRepository).findByUserId(1L);
    }

    @Test
    void getHealthRecords_UserNotFound() {
        // Arrange
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ovaPalService.getHealthRecords(999L)
        );
        
        assertTrue(exception.getMessage().contains("User not found"));
        verify(healthRecordRepository, never()).findByUserId(anyLong());
    }

    // More tests would be added for other methods...
} 