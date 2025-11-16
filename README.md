# Restaurant Management System - Backend

A comprehensive backend solution for restaurant management built with Spring Boot 3, PostgreSQL, and Docker.

## Overview

Restaurant Management System is a full-featured REST API for managing restaurant operations including:

- Menu Management (dishes, ingredients, recipes)
- Order Management (customer orders, order items, status tracking)
- Table Reservations (booking, availability checking)
- Inventory Management (ingredient stock, low stock alerts)
- Supply Chain (supplier management, deliveries, stock updates)
- Sales & Payments (transaction records, payment methods)
- User Management (staff roles, authentication)

This is the backend (server-side) component of the Restaurant Management System. It provides RESTful APIs for client applications to interact with restaurant data.

## Technology Stack

### Backend Framework
- Java 21
- Spring Boot 3.4.10
- Spring Data JPA - ORM for database operations
- Spring Security - Authentication & authorization
- Lombok - Reduce boilerplate code
- Spring Validation - Input validation

### Database
- PostgreSQL 16.11 - Primary database
- Flyway - Database migration management
- HikariCP - Connection pooling

### API & Documentation
- Springdoc OpenAPI 2.8.14 - Swagger/OpenAPI documentation
- REST API - JSON-based communication

### Container & Deployment
- Docker - Containerization
- Docker Compose - Multi-container orchestration

### Build & Dependency Management
- Gradle 8.0+ - Build tool with Kotlin DSL

### Additional Libraries
- ModelMapper - DTO mapping (optional)
- Spring AOP - Aspect-oriented programming for logging

## Project Structure

```
restaurant-system/
├── src/main/java/com/restaurant/system/
│   ├── config/              # Spring configurations (Security, Swagger, etc.)
│   ├── controller/          # REST API endpoints
│   ├── dto/                 # Data Transfer Objects with validation
│   ├── entity/              # JPA entities and enums
│   │   └── enums/           # OrderStatus, PaymentMethod, etc.
│   ├── exception/           # Custom exceptions and error handling
│   ├── repository/          # Spring Data JPA repositories
│   ├── security/            # JWT, roles, authentication
│   ├── service/             # Business logic interfaces
│   │   └── impl/            # Service implementations
│   ├── util/                # Utility classes (Logging, Mapping)
│   └── RestaurantSystemApplication.java
│
├── src/main/resources/
│   ├── db/migration/        # Flyway SQL migrations
│   │   ├── V1__create_tables.sql
│   │   ├── V2__add_triggers_and_functions.sql
│   │   └── V3__add_indexes.sql
│   └── application.yml      # Spring Boot configuration
│
├── docker-compose.yml       # Docker services definition
├── Dockerfile               # Docker image build
├── build.gradle.kts         # Gradle configuration
├── .gitignore               # Git ignore rules
└── README.md                # This file
```

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development without Docker)
- Gradle 8.0+ (for building locally)

### Using Docker (Recommended)

1. Clone the repository

```bash
git clone https://github.com/LastCh/restaurant-system.git
cd restaurant-system
```

2. Start all services

```bash
docker-compose up --build
```

3. Access the services

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- pgAdmin: http://localhost:5050
- Database: postgresql://localhost:5432/restaurant

### Local Development (without Docker)

1. Install PostgreSQL 16+

2. Set environment variables

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=restaurant
export DB_USER=admin
export DB_PASSWORD=secure_password_change_me
```

3. Build the project

```bash
./gradlew clean build -x test
```

4. Run the application

```bash
./gradlew bootRun
```

5. Access the application

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## API Documentation

### Available Endpoints

| Resource | Base URL | Methods | Description |
|----------|----------|---------|-------------|
| Clients | /api/clients | GET, POST, PUT, DELETE | Client management |
| Dishes | /api/dishes | GET, POST, PUT, DELETE | Menu items |
| Orders | /api/orders | GET, POST, PUT, DELETE | Order processing |
| Reservations | /api/reservations | GET, POST, PUT, DELETE | Table bookings |
| Ingredients | /api/ingredients | GET, POST, PUT, DELETE | Stock items |
| Supplies | /api/supplies | GET, POST, PUT, DELETE | Incoming deliveries |
| Sales | /api/sales | GET, POST, DELETE | Payment records |

### Example API Calls

Create a Client

```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "+79367087110"
  }'
```

Get All Clients

```bash
curl http://localhost:8080/api/clients
```

Get Client by ID

```bash
curl http://localhost:8080/api/clients/1
```

Update Client

```bash
curl -X PUT http://localhost:8080/api/clients/1 \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "phone": "+79367087111"
  }'
```

Delete Client

```bash
curl -X DELETE http://localhost:8080/api/clients/1
```

## Database Schema

### Core Tables
- clients - Customer information
- users - Staff and client authentication
- restaurant_tables - Physical dining tables
- reservations - Table bookings with status tracking
- dishes - Menu items with pricing
- ingredients - Stock items with quantities
- dish_ingredients - Recipe definitions (many-to-many)
- orders - Customer orders with totals
- order_items - Order line items with quantities
- sales - Payment records
- suppliers - Supplier information
- supplies - Incoming deliveries
- supply_items - Delivery line items

### Database Features
- Automatic timestamps (created_at, updated_at via @PrePersist/@PreUpdate)
- Triggers for:
    - Auto-calculation of order totals
    - Automatic stock adjustments
    - Sale creation on order completion
- Indexes for query optimization
- Constraints for data integrity (unique emails, positive prices, etc.)
- Enums for statuses (OrderStatus, PaymentMethod, ReservationStatus, SupplyStatus)

## Input Validation

All DTOs include comprehensive validation:

### Client Validation
- fullName: Required, letters and spaces only
- email: Required, must be valid email format
- phone: Required, E.164 format (e.g., +79367087110)

### Dish Validation
- name: Required
- price: Must be greater than 0
- preparationTimeMinutes: Minimum 1 minute

### Order Validation
- clientId: Required, must exist
- items: At least one item required
- quantity: Minimum 1 per item

### Error Response Example

```json
{
  "status": 400,
  "message": "email: Email should be valid; phone: Phone number should be in E.164 format",
  "error": "Validation Error",
  "path": "/api/clients",
  "timestamp": "2025-11-16T20:57:27.581Z"
}
```

## Security

### Current Implementation
- Spring Security for request authorization
- CORS configured for frontend integration
- STATELESS session management (ready for JWT)

### Features
- Public Swagger UI and API docs access
- All API endpoints currently public (for MVP)
- Password encoding ready (BCryptPasswordEncoder)

### Future Implementation
- JWT token authentication
- Role-based access control (@PreAuthorize)
- Login/Logout endpoints
- Refresh token mechanism

## Error Handling

All errors return consistent format:

```json
{
  "status": 400,
  "message": "Descriptive error message",
  "error": "Error Type",
  "path": "/api/endpoint",
  "timestamp": "2025-11-16T20:57:27.581Z"
}
```

### Handled Error Cases
- 400 Bad Request - Validation failures, illegal arguments
- 404 Not Found - Resource doesn't exist
- 409 Conflict - Duplicate email/phone/unique constraint violations
- 500 Internal Server Error - Unexpected errors with full stack trace

## Database Migrations

Migrations run automatically on application startup using Flyway:

### V1__create_tables.sql
- Create all entity tables
- Define primary keys and foreign keys
- Create enums and check constraints

### V2__add_triggers_and_functions.sql
- Database triggers for automatic calculations
- Functions for business logic enforcement
- Event tracking triggers

### V3__add_indexes.sql
- Performance indexes on frequently queried columns
- Unique constraints for business rules
- Foreign key indexes

## Configuration

### application.yml

```yaml
spring:
  jpa:
    open-in-view: false
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
```

### Environment Variables

```
DB_HOST=postgres
DB_PORT=5432
DB_NAME=restaurant
DB_USER=admin
DB_PASSWORD=secure_password_change_me
SPRING_PROFILES_ACTIVE=docker
```

## Logging

### Service Method Logging
All service method calls are logged via AOP:

```
Entering method: createClient with arguments: [ClientDTO(...)]
Exiting method: createClient
```

Enable with @Component on LoggingAspect class.

## Building & Deployment

### Local Build

```bash
./gradlew clean build -x test
```

### Docker Build

```bash
docker-compose build
```

### Production Considerations
- Environment variables for database credentials
- Disabled debug mode
- Proper logging configuration
- Database backups and monitoring
- API rate limiting
- Request/response logging

## Testing

### Testing Framework Setup
- JUnit 5 - Unit testing
- Mockito - Mocking framework
- Spring Boot Test - Integration testing

### Running Tests

```bash
./gradlew test
```

### Testing Endpoints with Curl

```bash
# Health check
curl http://localhost:8080/api/admin/health

# Create test client
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","phone":"+79367087110"}'
```

## Development Workflow

### 1. Create a feature branch

```bash
git checkout -b feature/your-feature-name
```

### 2. Make changes and test

```bash
./gradlew bootRun
```

### 3. Commit with descriptive messages

```bash
git add .
git commit -m "feat: add new feature

- Add X functionality
- Fix Y bug
- Update Z component"
```

### 4. Push and create Pull Request

```bash
git push origin feature/your-feature-name
```

## Troubleshooting

### Docker Issues

```bash
# View logs
docker logs restaurant-app

# Clean up and rebuild
docker-compose down -v
docker-compose up --build
```

### Database Connection Errors
- Ensure PostgreSQL is running
- Check .env file for correct credentials
- Verify port 5432 is available
- Run migrations: flyway migrate

### API Returns 500 Error
- Check application logs: docker logs restaurant-app
- Verify database is accessible
- Check request JSON format (valid JSON, no trailing commas)
- Ensure validation constraints are met

### Swagger UI Not Loading
- Clear browser cache
- Check springdoc.api-docs.enabled: true in application.yml
- Verify /v3/api-docs endpoint responds

## Roadmap

### Phase 1: Complete
- Core entity design
- REST API endpoints
- Input validation
- Error handling
- Database migrations

### Phase 2: In Progress
- Unique constraint validation
- Pagination and sorting
- Advanced filtering

### Phase 3: Planned
- JWT authentication
- Role-based access control
- WebSocket notifications
- Advanced reporting

### Phase 4: Future
- Redis caching
- API rate limiting
- Multi-restaurant support
- Payment gateway integration

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Author

Arthur Izm

## Support

For issues, bugs, or feature requests, please create an issue on GitHub.

## Version

Current Version: 0.0.1-SNAPSHOT

Java Version: 21

Spring Boot Version: 3.4.10

PostgreSQL Version: 16.11