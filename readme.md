# Expense Tracker API

A RESTful API built with Spring Boot to track personal expenses. This project demonstrates a clean architecture approach, integrating modern Java 21 features and a robust tech stack.

## Features

- **Expense Management**: Create, Read, Update, and Delete expenses.
- **Categorization**: Group expenses into categories (Food, Transport, Rent, Utility, Entertainment, Other).
- **Persistence**: Relational data storage using PostgreSQL.
- **Database Migrations**: Managed with Flyway.
- **API Documentation**: Interactive Swagger UI provided by SpringDoc OpenAPI.
- **Containerization**: Easy environment setup using Docker Compose.

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.2**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway DB**
- **SpringDoc OpenAPI (Swagger UI)**
- **Testcontainers** (for integration testing)
- **Maven**

## Prerequisites

- [JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Docker & Docker Compose](https://docs.docker.com/get-docker/)
- [Maven](https://maven.apache.org/download.cgi) (optional, wrapper can be used)

## Getting Started

### 1. Database Setup

The project uses PostgreSQL. You can spin up the database using the provided Docker Compose file:

```bash
docker-compose -f docker/docker-compose.yml up -d
```

The database will be available at `localhost:5433` with the following credentials:
- **Database**: `expense`
- **Username**: `expense`
- **Password**: `password`

### 2. Build and Run

You can run the application using the Spring Boot Maven plugin:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Documentation

Once the application is running, you can access the interactive API documentation (Swagger UI) at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/expenses` | Create a new expense |
| `GET` | `/api/expenses` | List all expenses |
| `GET` | `/api/expenses/{id}` | Get details of a specific expense |
| `PUT` | `/api/expenses/{id}` | Update an existing expense |
| `DELETE` | `/api/expenses/{id}` | Delete an expense |

### Sample Request Body (Create/Update)

```json
{
  "category": "FOOD",
  "description": "Lunch at work",
  "amount": 15.50,
  "date": "2026-02-04"
}
```

## Project Structure

The project follows a layered architecture:

- `application`: Contains DTOs, commands, and application services.
- `domain`: Contains business logic, entities, and repository interfaces.
- `infrastructure`: Implementation of persistence and other infrastructure concerns.
- `interfaces`: REST controllers and API-related components.
- `config`: Spring configuration classes.

## Database Migrations

Database schema changes are managed by Flyway. Migrations are located in `src/main/resources/db/migration`. They are automatically applied when the application starts.
# expense-tracker
