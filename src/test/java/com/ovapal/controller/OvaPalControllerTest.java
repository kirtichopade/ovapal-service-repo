package com.ovapal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovapal.bean.*;
import com.ovapal.bean.request.UserRequestBean;
import com.ovapal.entity.*;
import com.ovapal.service.OvaPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OvaPalController.class)
public class OvaPalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OvaPalService ovaPalService;

    private UserRequestBean userRequestBean;
    private UserResponseBean userResponseBean;
    private LoginRequestBean loginRequestBean;
    private HealthRecordRequestBean healthRecordRequestBean;
    private HealthRecordResponseBean healthRecordResponseBean;
    private PeriodRecordRequestBean periodRecordRequestBean;
    private PeriodRecordResponseBean periodRecordResponseBean;
    private ReminderRequestBean reminderRequestBean;
    private ReminderResponseBean reminderResponseBean;
    private MedicationRequestBean medicationRequestBean;
    private MedicationResponseBean medicationResponseBean;

    @BeforeEach
    void setUp() {
        // Set up user request/response
        userRequestBean = UserRequestBean.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .age(25)
                .build();

        userResponseBean = UserResponseBean.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .build();

        loginRequestBean = LoginRequestBean.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // Set up health record request/response
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

        healthRecordResponseBean = HealthRecordResponseBean.builder()
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

        // Set up period record request/response
        periodRecordRequestBean = PeriodRecordRequestBean.builder()
                .userId(1L)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now())
                .flow("Medium")
                .symptoms("Cramps")
                .mood("Normal")
                .notes("Regular cycle")
                .build();

        periodRecordResponseBean = PeriodRecordResponseBean.builder()
                .id(1L)
                .userId(1L)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now())
                .flow("Medium")
                .symptoms("Cramps")
                .mood("Normal")
                .notes("Regular cycle")
                .build();

        // Set up reminder request/response
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

        reminderResponseBean = ReminderResponseBean.builder()
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

        // Set up medication request/response
        medicationRequestBean = MedicationRequestBean.builder()
                .userId(1L)
                .medicinename("Ibuprofen")
                .dosage("200mg")
                .frequency("Twice daily")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .notes("Take with food")
                .build();

        medicationResponseBean = MedicationResponseBean.builder()
                .medicationid(1L)
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
    void createUser_ReturnsCreatedUser() throws Exception {
        // Arrange
        when(ovaPalService.createUser(new UserRequestBean())).thenReturn(userResponseBean);

        // Act & Assert
        mockMvc.perform(post("/ovapal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestBean)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.age", is(25)));

        verify(ovaPalService).createUser(new UserRequestBean());
    }

    @Test
    void login_ReturnsUser() throws Exception {
        // Arrange
        when(ovaPalService.loginUser(new LoginRequestBean())).thenReturn(userResponseBean);

        // Act & Assert
        mockMvc.perform(post("/ovapal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestBean)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(ovaPalService).loginUser(new LoginRequestBean());
    }

    @Test
    void getHealthRecords_ReturnsHealthRecords() throws Exception {
        // Arrange
        List<HealthRecordResponseBean> healthRecords = Arrays.asList(healthRecordResponseBean);
        when(ovaPalService.getHealthRecords(anyLong())).thenReturn(healthRecords);

        // Act & Assert
        mockMvc.perform(get("/ovapal/health/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].weight", is(60.5)));

        verify(ovaPalService).getHealthRecords(1L);
    }

    @Test
    void savePeriodRecord_ReturnsSavedRecord() throws Exception {
        // Arrange
        when(ovaPalService.savePeriodRecord(new PeriodRecordRequestBean())).thenReturn(periodRecordResponseBean);

        // Act & Assert
        mockMvc.perform(post("/ovapal/period")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(periodRecordRequestBean)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.flow", is("Medium")))
                .andExpect(jsonPath("$.symptoms", is("Cramps")));

        verify(ovaPalService).savePeriodRecord(new PeriodRecordRequestBean());
    }

    @Test
    void updatePeriodRecord_ReturnsUpdatedRecord() throws Exception {
        // Arrange
        when(ovaPalService.updatePeriodRecord(anyLong(), new PeriodRecordRequestBean()))
                .thenReturn(periodRecordResponseBean);

        // Act & Assert
        mockMvc.perform(put("/ovapal/period/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(periodRecordRequestBean)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.flow", is("Medium")));

        verify(ovaPalService).updatePeriodRecord(eq(1L), new PeriodRecordRequestBean());
    }

    // More tests would be added for other controller methods...
} 