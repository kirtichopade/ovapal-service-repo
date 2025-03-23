package com.ovapal.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ovapal.bean.request.UserRequestBean;
import com.ovapal.service.OvaPalService;
import com.ovapal.util.TestUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OvaPalService ovaPalService;

    @Test
    void handleResourceNotFoundException() throws Exception {
        // Arrange
        when(ovaPalService.getHealthRecords(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Health record not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(post("/ovapal/health/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Health record not found with ID: 999"));
    }

    @Test
    void handleInvalidOperationException() throws Exception {
        // Arrange
        UserRequestBean invalidUser = UserRequestBean.builder()
                .name("Test User")
                .email("invalid-email")
                .password("pass")  // Too short
                .age(25)
                .build();

        when(ovaPalService.createUser(any(UserRequestBean.class)))
                .thenThrow(new InvalidOperationException("Invalid email format"));

        // Act & Assert
        mockMvc.perform(post("/ovapal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.asJsonString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    void handleAuthenticationException() throws Exception {
        // Arrange
        when(ovaPalService.loginUser(any()))
                .thenThrow(new AuthenticationException("Invalid email or password"));

        // Act & Assert
        mockMvc.perform(post("/ovapal/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void handleGenericException() throws Exception {
        // Arrange
        when(ovaPalService.createUser(any(UserRequestBean.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/ovapal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.asJsonString(TestUtil.createTestUserRequestBean())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
} 