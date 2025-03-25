package com.ovapal.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ovapal.bean.request.UserRequestBean;
import com.ovapal.entity.User;
import com.ovapal.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for OvaPal application.
 * These tests use an in-memory database to test the full flow from
 * controller through service to repository.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OvaPalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        // Clean up the database after each test
        userRepository.deleteAll();
    }

    @Test
    void createUser_Integration_Success() throws Exception {
        // Arrange
        UserRequestBean userRequest = UserRequestBean.builder()
                .name("Integration Test User")
                .email("integration-test@example.com")
                .password("password123")
                .age(30)
                .build();

        // Act & Assert
        mockMvc.perform(post("/ovapal/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.name", is("Integration Test User")))
                .andExpect(jsonPath("$.email", is("integration-test@example.com")))
                .andExpect(jsonPath("$.age", is(30)));

        // Verify that the user was actually saved in the database
        User savedUser = userRepository.findByEmail("integration-test@example.com").orElseThrow();
        assert savedUser.getName().equals("Integration Test User");
    }

    // Additional integration tests for other endpoints would be added here
} 