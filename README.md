# OvaPal Service

OvaPal Service is a backend service for the OvaPal application, a comprehensive health tracking and management solution focused on women's health.

## Features

- **User Management**: Create and authenticate users
- **Health Records**: Track personal health metrics
- **Period Tracking**: Monitor menstrual cycles
- **Reminders**: Set and manage medication and health reminders
- **Medication Management**: Track medications and their schedules

## Technical Stack

- Java 17
- Spring Boot
- Spring Data JPA
- RESTful API architecture
- Clean architecture with DTOs for separation of concerns

## Project Structure

The project follows a layered architecture:

- `com.ovapal.controller`: REST API endpoints
- `com.ovapal.service`: Business logic implementation
- `com.ovapal.repository`: Data access interfaces
- `com.ovapal.entity`: Domain models/entities
- `com.ovapal.bean`: Request and response data transfer objects
- `com.ovapal.exception`: Custom exception handling

## Testing

The project includes comprehensive tests:

- Unit tests for service and controller layers
- Integration tests for end-to-end functionality
- Exception handling tests
- Test utilities for test data generation

## Getting Started

### Prerequisites

- Java JDK 17 or higher
- Maven 3.6 or higher

### Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

### Running Tests

```bash
mvn test
```

## API Documentation

The API provides endpoints for:

- User creation and authentication
- Health record management
- Period tracking
- Reminder management
- Medication tracking

For detailed API documentation, run the application and visit `/swagger-ui.html`. 