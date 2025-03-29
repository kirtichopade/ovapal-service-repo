package com.ovapal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovapal.bean.*;
import com.ovapal.exception.ResourceNotFoundException;
import com.ovapal.service.OvaPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OvaPalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OvaPalService ovaPalService;

    private UserResponseBean userResponse;
    private LoginResponse loginResponse;
    private HealthRecordResponseBean healthRecordResponse;
    private PeriodRecordResponseBean periodRecordResponse;
    private ReminderResponseBean reminderResponse;
    private MedicationResponseBean medicationResponse;

    @BeforeEach
    void setUp() {
        // Initialize common response objects
        userResponse = new UserResponseBean();
        userResponse.setUserId(1L);
        userResponse.setName("test");
        userResponse.setEmail("testuser@gmail.com");

        loginResponse = new LoginResponse();
        loginResponse.setUser(userResponse);
        loginResponse.setToken("dummy-token");

        healthRecordResponse = new HealthRecordResponseBean();
        healthRecordResponse.setHealthId(1L);
        healthRecordResponse.setUserId(1L);

        periodRecordResponse = new PeriodRecordResponseBean();
        periodRecordResponse.setPeriodRecId(1L);
        periodRecordResponse.setUserId(1L);

        reminderResponse = new ReminderResponseBean();
        reminderResponse.setReminderId(1L);
        reminderResponse.setUserId(1L);

        medicationResponse = new MedicationResponseBean();
        medicationResponse.setMedicationId(1L);
        medicationResponse.setUserId(1L);
    }

    // User Management Tests
    @Test
    void createUser_ShouldReturnCreatedUserWithStatus201() throws Exception {
        UserRequestBean request = new UserRequestBean();
        request.setEmail("newuser@gmail.com");
        request.setPassword("password");

        when(ovaPalService.createUser(any(UserRequestBean.class))).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/ovapal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.email", is("testuser@gmail.com")));
    }

    @Test
    void login_ShouldReturnUserDetailsAndToken() throws Exception {
        LoginRequestBean request = new LoginRequestBean();
        request.setEmail("testuser@gmail.com");
        request.setPassword("password");

        when(ovaPalService.loginUser(any(LoginRequestBean.class))).thenReturn(loginResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/ovapal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.userId", is(1)))
                .andExpect(jsonPath("$.user.email", is("testuser@gmail.com")))
                .andExpect(jsonPath("$.token", is("dummy-token")));
    }

    // Health Record Tests
    @Test
    void createHealthRecord_ShouldReturnCreatedRecord() throws Exception {
        HealthRecordRequestBean request = new HealthRecordRequestBean();
        request.setUserId(1L);

        when(ovaPalService.saveHealthRecord(any(HealthRecordRequestBean.class))).thenReturn(healthRecordResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/ovapal/health")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void getHealthRecords_ShouldReturnListOfRecords() throws Exception {
        List<HealthRecordResponseBean> records = Arrays.asList(healthRecordResponse);
        when(ovaPalService.getHealthRecords(anyLong())).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/health/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].healthId", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)));
    }

    @Test
    void updateHealthRecord_ShouldReturnUpdatedRecord() throws Exception {
        HealthRecordRequestBean request = new HealthRecordRequestBean();
        request.setUserId(1L);

        when(ovaPalService.updateHealthRecord(anyLong(), any(HealthRecordRequestBean.class)))
                .thenReturn(healthRecordResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/ovapal/health/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void getHealthRecords_ShouldReturnEmptyListForUserWithNoRecords() throws Exception {
        when(ovaPalService.getHealthRecords(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/health/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Period Record Tests
    @Test
    void getPeriodRecords_ShouldReturnListOfRecords() throws Exception {
        List<PeriodRecordResponseBean> records = Arrays.asList(periodRecordResponse);
        when(ovaPalService.getPeriodRecords(anyLong())).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/period/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].periodRecId", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)));
    }

    @Test
    void savePeriodRecord_ShouldReturnSavedRecord() throws Exception {
        PeriodRecordRequestBean request = new PeriodRecordRequestBean();
        request.setUserId(1L);

        when(ovaPalService.savePeriodRecord(any(PeriodRecordRequestBean.class))).thenReturn(periodRecordResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/ovapal/period")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodRecId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void updatePeriodRecord_ShouldReturnUpdatedRecord() throws Exception {
        PeriodRecordRequestBean request = new PeriodRecordRequestBean();
        request.setUserId(1L);

        when(ovaPalService.updatePeriodRecord(anyLong(), any(PeriodRecordRequestBean.class)))
                .thenReturn(periodRecordResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/ovapal/period/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodRecId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void getPeriodRecords_ShouldReturnNotFoundForInvalidUser() throws Exception {
        when(ovaPalService.getPeriodRecords(anyLong())).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/period/999"))
                .andExpect(status().isNotFound());
    }

    // Reminder Tests
    @Test
    void getReminders_ShouldReturnListOfReminders() throws Exception {
        List<ReminderResponseBean> reminders = Arrays.asList(reminderResponse);
        when(ovaPalService.getReminders(anyLong())).thenReturn(reminders);

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/reminders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reminderId", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)));
    }

    @Test
    void setReminder_ShouldReturnCreatedReminder() throws Exception {
        ReminderRequestBean request = new ReminderRequestBean();
        request.setUserId(1L);

        when(ovaPalService.setReminder(any(ReminderRequestBean.class))).thenReturn(reminderResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/ovapal/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reminderId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void updateReminder_ShouldReturnUpdatedReminder() throws Exception {
        ReminderRequestBean request = new ReminderRequestBean();
        request.setUserId(1L);

        when(ovaPalService.updateReminder(anyLong(), any(ReminderRequestBean.class)))
                .thenReturn(reminderResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/ovapal/reminders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reminderId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void deleteReminder_ShouldReturnSuccess() throws Exception {
        doNothing().when(ovaPalService).deleteReminder(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/ovapal/reminders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Reminder deleted successfully")));

        verify(ovaPalService, times(1)).deleteReminder(1L);
    }

    @Test
    void getReminders_ShouldReturnNotFoundForInvalidUser() throws Exception {
        when(ovaPalService.getReminders(anyLong())).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/reminders/999"))
                .andExpect(status().isNotFound());
    }

    // Medication Tests
    @Test
    void getMedications_ShouldReturnListOfMedications() throws Exception {
        List<MedicationResponseBean> medications = Arrays.asList(medicationResponse);
        when(ovaPalService.getMedications(anyLong())).thenReturn(medications);

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/medications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].medicationId", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)));
    }

    @Test
    void addMedication_ShouldReturnCreatedMedication() throws Exception {
        MedicationRequestBean request = new MedicationRequestBean();
        request.setUserId(1L);

        when(ovaPalService.addMedication(any(MedicationRequestBean.class))).thenReturn(medicationResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/ovapal/medications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void updateMedication_ShouldReturnUpdatedMedication() throws Exception {
        MedicationRequestBean request = new MedicationRequestBean();
        request.setUserId(1L);

        when(ovaPalService.updateMedication(anyLong(), any(MedicationRequestBean.class)))
                .thenReturn(medicationResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/ovapal/medications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void deleteMedication_ShouldReturnSuccess() throws Exception {
        doNothing().when(ovaPalService).deleteMedication(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/ovapal/medications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Medication deleted successfully")));

        verify(ovaPalService, times(1)).deleteMedication(1L);
    }

    @Test
    void getMedications_ShouldReturnNotFoundForInvalidUser() throws Exception {
        when(ovaPalService.getMedications(anyLong())).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/ovapal/medications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateHealthRecord_ShouldReturnNotFoundForInvalidId() throws Exception {
        HealthRecordRequestBean request = new HealthRecordRequestBean();
        request.setUserId(1L);

        when(ovaPalService.updateHealthRecord(anyLong(), any(HealthRecordRequestBean.class)))
                .thenThrow(new ResourceNotFoundException("Health record not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/ovapal/health/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}