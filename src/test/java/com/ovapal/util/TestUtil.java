package com.ovapal.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ovapal.bean.request.UserRequestBean;
import com.ovapal.entity.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

/**
 * Utility class for testing.
 * Contains helper methods for creating test objects and other test-related utilities.
 */
public class TestUtil {
    
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    /**
     * Creates a test User entity with random values.
     * @return A User entity
     */
    public static User createTestUser() {
        return User.builder()
                .userid(random.nextLong(1000))
                .name("Test User " + random.nextInt(1000))
                .email("test" + random.nextInt(1000) + "@example.com")
                .password("password" + random.nextInt(1000))
                .age(20 + random.nextInt(50))
                .build();
    }
    
    /**
     * Creates a test UserRequestBean with random values.
     * @return A UserRequestBean
     */
    public static UserRequestBean createTestUserRequestBean() {
        return UserRequestBean.builder()
                .name("Test User " + random.nextInt(1000))
                .email("test" + random.nextInt(1000) + "@example.com")
                .password("password" + random.nextInt(1000))
                .age(20 + random.nextInt(50))
                .build();
    }
    
    /**
     * Creates a test HealthRecord entity with random values for the given user ID.
     * @param userId The user ID
     * @return A HealthRecord entity
     */
    public static HealthRecord createTestHealthRecord(Long userId) {
        return HealthRecord.builder()
                .healthId(random.nextLong(1000))
                .userId(userId)
                .recordDate(LocalDate.now().minusDays(random.nextInt(30)))
                .weight(50 + random.nextDouble() * 50)
                .height(150 + random.nextDouble() * 50)
                .temperature(36 + random.nextDouble() * 2)
                .heartRate(60 + random.nextInt(60))
                .bloodPressureSystolic(100 + random.nextInt(50))
                .bloodPressureDiastolic(60 + random.nextInt(30))
                .notes("Test health record " + random.nextInt(1000))
                .build();
    }
    
    /**
     * Creates a test PeriodRecord entity with random values for the given user ID.
     * @param userId The user ID
     * @return A PeriodRecord entity
     */
    public static PeriodRecord createTestPeriodRecord(Long userId) {
        LocalDate startDate = LocalDate.now().minusDays(random.nextInt(30));
        return PeriodRecord.builder()
                .periodrecid(random.nextLong(1000))
                .userId(userId)
                .startDate(startDate)
                .endDate(startDate.plusDays(3 + random.nextInt(5)))
                .flow(getRandomFlow())
                .symptoms("Test symptoms " + random.nextInt(1000))
                .mood("Test mood " + random.nextInt(1000))
                .notes("Test period record " + random.nextInt(1000))
                .build();
    }
    
    /**
     * Creates a test Reminder entity with random values for the given user ID.
     * @param userId The user ID
     * @return A Reminder entity
     */
    public static Reminder createTestReminder(Long userId) {
        return Reminder.builder()
                .reminderid(random.nextLong(1000))
                .userId(userId)
                .title("Test reminder " + random.nextInt(1000))
                .description("Test description " + random.nextInt(1000))
                .reminderDate(LocalDate.now().plusDays(random.nextInt(30)))
                .reminderTime(LocalTime.of(random.nextInt(24), random.nextInt(60)))
                .isRepeating(random.nextBoolean())
                .repeatFrequency(getRandomFrequency())
                .isActive(true)
                .build();
    }
    
    /**
     * Creates a test Medication entity with random values for the given user ID.
     * @param userId The user ID
     * @return A Medication entity
     */
    public static Medication createTestMedication(Long userId) {
        LocalDate startDate = LocalDate.now();
        return Medication.builder()
                .medicineid(random.nextLong(1000))
                .userId(userId)
                .medicine("Test medication " + random.nextInt(1000))
                .dosage(random.nextInt(500) + "mg")
                .frequency(getRandomFrequency())
                .startDate(startDate)
                .endDate(startDate.plusDays(7 + random.nextInt(30)))
                .notes("Test medication notes " + random.nextInt(1000))
                .build();
    }
    
    /**
     * Converts an object to a JSON string.
     * @param object The object to convert
     * @return The JSON string
     */
    public static String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String getRandomFlow() {
        String[] flows = {"Light", "Medium", "Heavy"};
        return flows[random.nextInt(flows.length)];
    }
    
    private static String getRandomFrequency() {
        String[] frequencies = {"Daily", "Weekly", "Monthly", "Twice daily", "Every 8 hours"};
        return frequencies[random.nextInt(frequencies.length)];
    }
} 