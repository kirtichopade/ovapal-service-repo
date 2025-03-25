package com.ovapal.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        String errorMessage = "Resource not found";
        when(webRequest.getDescription(false)).thenReturn("uri=/api/resource/1");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleResourceNotFoundException(
                new ResourceNotFoundException(errorMessage), webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"));
        assertEquals("Not Found", body.get("error"));
        assertEquals(errorMessage, body.get("message"));
        assertEquals("uri=/api/resource/1", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleInvalidOperationException_ShouldReturnBadRequestResponse() {
        // Arrange
        String errorMessage = "Invalid operation";
        when(webRequest.getDescription(false)).thenReturn("uri=/api/operation");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleInvalidOperationException(
                new InvalidOperationException(errorMessage), webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals(errorMessage, body.get("message"));
        assertEquals("uri=/api/operation", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleAuthenticationException_ShouldReturnUnauthorizedResponse() {
        // Arrange
        String errorMessage = "Authentication failed";
        when(webRequest.getDescription(false)).thenReturn("uri=/api/auth");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAuthenticationException(
                new AuthenticationException(errorMessage), webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals(errorMessage, body.get("message"));
        assertEquals("uri=/api/auth", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        String errorMessage = "Unexpected error occurred";
        when(webRequest.getDescription(false)).thenReturn("uri=/api/endpoint");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleGlobalException(
                new Exception(errorMessage), webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals(errorMessage, body.get("message"));
        assertEquals("uri=/api/endpoint", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void buildErrorResponse_ShouldContainCorrectStructure() {
        // Arrange
        String errorMessage = "Test error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.buildErrorResponse(
                new RuntimeException(errorMessage), status, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(status, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(5, body.size());
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("path"));

        assertEquals(status.value(), body.get("status"));
        assertEquals(status.getReasonPhrase(), body.get("error"));
        assertEquals(errorMessage, body.get("message"));
        assertEquals("uri=/test", body.get("path"));
    }

    @Test
    void buildErrorResponse_ShouldUseCurrentTimestamp() {
        // Arrange
        LocalDateTime beforeTest = LocalDateTime.now();
        when(webRequest.getDescription(false)).thenReturn("uri=/test");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.buildErrorResponse(
                new RuntimeException("Test"), HttpStatus.BAD_REQUEST, webRequest);
        LocalDateTime afterTest = LocalDateTime.now();

        // Assert
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        LocalDateTime timestamp = LocalDateTime.parse(body.get("timestamp").toString());

        assertTrue(timestamp.isAfter(beforeTest) || timestamp.isEqual(beforeTest));
        assertTrue(timestamp.isBefore(afterTest) || timestamp.isEqual(afterTest));
    }
}