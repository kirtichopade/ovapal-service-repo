package com.ovapal.controller;

import com.ovapal.bean.*;
import com.ovapal.service.OvaPalService;
import com.ovapal.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class OvaPalControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OvaPalService ovaPalService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private OvaPalController ovaPalController;

    private final String validToken = "valid.token.here";
    private final String invalidToken = "invalid.token";
    private final Long testUserId = 1L;
    private final Long testRecordId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ovaPalController).build();

        // Mock token validation
        when(jwtTokenUtil.validateToken(validToken)).thenReturn(true);
        when(jwtTokenUtil.validateToken(invalidToken)).thenReturn(false);
    }

    // Test helper methods
    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    // 1. User Registration
    @Test
    void createUser_ShouldReturnUserResponse() throws Exception {
        UserRequestBean request = new UserRequestBean();
        UserResponseBean response = new UserResponseBean();
        response.setUserId(testUserId);

        when(ovaPalService.createUser(any(UserRequestBean.class))).thenReturn(response);

        mockMvc.perform(post("/ovapal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId));
    }

    // 2. Login
    @Test
    void login_ShouldReturnTokenAndUser() throws Exception {
        LoginRequestBean request = new LoginRequestBean();
        LoginResponseBean loginResponse = new LoginResponseBean();
        UserResponseBean user = new UserResponseBean();
        user.setUserId(testUserId);
        loginResponse.setUser(user);

        when(ovaPalService.loginUser(any(LoginRequestBean.class))).thenReturn(loginResponse);
        when(jwtTokenUtil.generateToken(testUserId)).thenReturn(validToken);

        mockMvc.perform(post("/ovapal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.userId").value(testUserId));
    }

    // 3. Health Record Endpoints
    @Test
    void createHealthRecord_WithValidToken_ShouldReturnRecord() throws Exception {
        HealthRecordRequestBean request = new HealthRecordRequestBean();
        HealthRecordResponseBean response = new HealthRecordResponseBean();
        response.setHealthId(testRecordId);

        when(ovaPalService.saveHealthRecord(any(HealthRecordRequestBean.class))).thenReturn(response);

        mockMvc.perform(post("/ovapal/health")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthId").value(testRecordId));
    }

    @Test
    void getHealthRecords_WithValidToken_ShouldReturnRecords() throws Exception {
        HealthRecordResponseBean record = new HealthRecordResponseBean();
        List<HealthRecordResponseBean> records = Arrays.asList(record);

        when(ovaPalService.getHealthRecords(testUserId)).thenReturn(records);

        mockMvc.perform(get("/ovapal/health/" + testUserId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void updateHealthRecord_WithValidToken_ShouldReturnUpdatedRecord() throws Exception {
        HealthRecordRequestBean request = new HealthRecordRequestBean();
        HealthRecordResponseBean response = new HealthRecordResponseBean();
        response.setHealthId(testRecordId);

        when(ovaPalService.updateHealthRecord(anyLong(), any(HealthRecordRequestBean.class))).thenReturn(response);

        mockMvc.perform(put("/ovapal/health/" + testRecordId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthId").value(testRecordId));
    }

    // 4. Period Record Endpoints
    @Test
    void getPeriodRecords_WithValidToken_ShouldReturnRecords() throws Exception {
        PeriodRecordResponseBean record = new PeriodRecordResponseBean();
        List<PeriodRecordResponseBean> records = Arrays.asList(record);

        when(ovaPalService.getPeriodRecords(testUserId)).thenReturn(records);

        mockMvc.perform(get("/ovapal/period/" + testUserId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void savePeriodRecord_WithValidToken_ShouldReturnRecord() throws Exception {
        PeriodRecordRequestBean request = new PeriodRecordRequestBean();
        PeriodRecordResponseBean response = new PeriodRecordResponseBean();
        response.setPeriodRecId(testRecordId);

        when(ovaPalService.savePeriodRecord(any(PeriodRecordRequestBean.class))).thenReturn(response);

        mockMvc.perform(post("/ovapal/period")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodRecId").value(testRecordId));
    }

    @Test
    void updatePeriodRecord_WithValidToken_ShouldReturnUpdatedRecord() throws Exception {
        PeriodRecordRequestBean request = new PeriodRecordRequestBean();
        PeriodRecordResponseBean response = new PeriodRecordResponseBean();
        response.setPeriodRecId(testRecordId);

        when(ovaPalService.updatePeriodRecord(anyLong(), any(PeriodRecordRequestBean.class))).thenReturn(response);

        mockMvc.perform(put("/ovapal/period/" + testRecordId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodRecId").value(testRecordId));
    }

    // 5. Reminder Endpoints
    @Test
    void getReminders_WithValidToken_ShouldReturnReminders() throws Exception {
        ReminderResponseBean reminder = new ReminderResponseBean();
        List<ReminderResponseBean> reminders = Arrays.asList(reminder);

        when(ovaPalService.getReminders(testUserId)).thenReturn(reminders);

        mockMvc.perform(get("/ovapal/reminders/" + testUserId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void setReminder_WithValidToken_ShouldReturnReminder() throws Exception {
        ReminderRequestBean request = new ReminderRequestBean();
        ReminderResponseBean response = new ReminderResponseBean();
        response.setReminderId(testRecordId);

        when(ovaPalService.setReminder(any(ReminderRequestBean.class))).thenReturn(response);

        mockMvc.perform(post("/ovapal/reminders")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reminderId").value(testRecordId));
    }

    @Test
    void updateReminder_WithValidToken_ShouldReturnUpdatedReminder() throws Exception {
        ReminderRequestBean request = new ReminderRequestBean();
        ReminderResponseBean response = new ReminderResponseBean();
        response.setReminderId(testRecordId);

        when(ovaPalService.updateReminder(anyLong(), any(ReminderRequestBean.class))).thenReturn(response);

        mockMvc.perform(put("/ovapal/reminders/" + testRecordId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reminderId").value(testRecordId));
    }

    @Test
    void deleteReminder_WithValidToken_ShouldReturnSuccess() throws Exception {
        doNothing().when(ovaPalService).deleteReminder(testRecordId);

        mockMvc.perform(delete("/ovapal/reminders/" + testRecordId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Reminder deleted successfully"));
    }

    // 6. Medication Endpoints
    @Test
    void addMedication_WithValidToken_ShouldReturnMedication() throws Exception {
        MedicationRequestBean request = new MedicationRequestBean();
        MedicationResponseBean response = new MedicationResponseBean();
        response.setMedicationId(testRecordId);

        when(ovaPalService.addMedication(any(MedicationRequestBean.class))).thenReturn(response);

        mockMvc.perform(post("/ovapal/medications")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId").value(testRecordId));
    }

    @Test
    void getMedications_WithValidToken_ShouldReturnMedications() throws Exception {
        MedicationResponseBean medication = new MedicationResponseBean();
        List<MedicationResponseBean> medications = Arrays.asList(medication);

        when(ovaPalService.getMedications(testUserId)).thenReturn(medications);

        mockMvc.perform(get("/ovapal/medications/" + testUserId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void updateMedication_WithValidToken_ShouldReturnUpdatedMedication() throws Exception {
        MedicationRequestBean request = new MedicationRequestBean();
        MedicationResponseBean response = new MedicationResponseBean();
        response.setMedicationId(testRecordId);

        when(ovaPalService.updateMedication(anyLong(), any(MedicationRequestBean.class))).thenReturn(response);

        mockMvc.perform(put("/ovapal/medications/" + testRecordId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId").value(testRecordId));
    }

    @Test
    void deleteMedication_WithValidToken_ShouldReturnSuccess() throws Exception {
        doNothing().when(ovaPalService).deleteMedication(testRecordId);

        mockMvc.perform(delete("/ovapal/medications/" + testRecordId)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Medication deleted successfully"));
    }

    // Token Validation Tests
    @Test
    void protectedEndpoint_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/ovapal/health/" + testUserId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void protectedEndpoint_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/ovapal/health/" + testUserId)
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }
}