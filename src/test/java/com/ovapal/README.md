# OvaPal Test Package

This directory contains tests for the OvaPal service application.

## Test Structure

The tests are organized into several categories:

### Unit Tests
Located in their respective packages, these tests focus on testing individual components in isolation:

- `com.ovapal.service.OvaPalServiceTest`: Tests for the service layer with mocked dependencies
- `com.ovapal.controller.OvaPalControllerTest`: Tests for the controller layer with mocked service layer

### Integration Tests
Located in the `com.ovapal.integration` package, these tests verify the interaction between multiple components:

- `com.ovapal.integration.OvaPalIntegrationTest`: Tests the full flow from controller to repository using an in-memory database

### Exception Handler Tests
Located in the `com.ovapal.exception` package, these tests verify the global exception handling:

- `com.ovapal.exception.GlobalExceptionHandlerTest`: Tests for exception handling

### Test Utilities
Located in the `com.ovapal.util` package, these provide common functionality for tests:

- `com.ovapal.util.TestUtil`: Utility methods for creating test objects and other test-related utilities

## Configuration

The test configuration is located in `src/test/resources/application-test.yml` and uses:

- H2 in-memory database for testing
- Hibernate with create-drop mode to create a fresh database for each test run
- Detailed SQL logging for debugging

## Running Tests

Run the tests using Maven:

```bash
./mvnw test
```

Or using your IDE's test runner.

## Adding New Tests

When adding new tests, follow these guidelines:

1. Unit tests should use Mockito to mock dependencies
2. Integration tests should use the `@SpringBootTest` annotation with the test profile
3. Test methods should follow the naming convention: `methodName_condition_expectedResult`
4. Use the test utilities to create test objects when possible 