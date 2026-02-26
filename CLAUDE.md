# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Local Database
```bash
docker-compose -f docker/docker-compose.yml up -d
```
PostgreSQL runs on `localhost:5433`, database `expense-tracker`, user `expense_user`, password `password`.

### Build & Run
```bash
./mvnw spring-boot:run        # Run the application
./mvnw clean package          # Build the JAR
```

### Testing
```bash
./mvnw test                                              # Run all tests
./mvnw test -Dtest=CreateExpenseServiceTest              # Run a single test class
./mvnw test -Dtest=CreateExpenseServiceTest#methodName   # Run a single test method
```
Integration tests (`ExpenseRepositoryIntegrationTest`) require Docker to spin up a real PostgreSQL container via Testcontainers.

### Linting & Formatting
```bash
./mvnw checkstyle:check          # Run Checkstyle (also runs on validate phase)
./mvnw spotless:apply            # Auto-format code with Spotless
./mvnw spotless:check            # Check formatting without modifying
```
Checkstyle runs automatically during the `validate` phase (before compile). Violations fail the build.

## Architecture

The project uses a strict layered clean architecture. Dependencies flow inward: `interfaces` → `application` → `domain` ← `infrastructure`.

### `domain`
Pure Java (no Spring/JPA annotations). Contains:
- **Records** as immutable domain models: `User`, `Expense`, `Money` (value object)
- **Repository interfaces**: `UserRepository`, `ExpenseRepository` — define the contract, no implementation here
- **Domain exceptions**: `ExpenseNotFoundException`, `DuplicateEmailException`, `InvalidCredentialsException`
- **Enums**: `Role`, `AuthProvider`, `ExpenseCategory`

### `application`
Application services (use cases) and DTOs. One service class per operation (e.g., `CreateExpenseService`, `GetExpenseService`). Services depend only on domain interfaces.
- `dto/request` — HTTP-facing request bodies with validation annotations
- `dto/command` — Internal transfer objects from request to service (via `req.toCommand()`)
- `dto/response` — API response shapes (e.g., `AuthResponse`)
- `port/TokenService` — Port interface for JWT operations (implemented in infrastructure)

### `infrastructure`
- **`persistence/`**: Each aggregate has a `*JpaEntity` (annotated JPA), `*JpaRepository` (Spring Data), `*RepositoryImpl` (implements domain interface), and `*Mapper` (converts between domain record ↔ JPA entity)
- **`security/`**: `JwtTokenService` (implements `TokenService` using Nimbus JOSE), `JwtAuthenticationFilter` (validates JWT per-request and sets `Authentication.getPrincipal()` to a `User` record)

### `interfaces/rest`
REST controllers and `GlobalExceptionHandler`. Controllers extract the authenticated `User` from `Authentication.getPrincipal()` and delegate to application services. Error responses use RFC 9457 `ProblemDetail`.

### `config`
Spring configuration: `SecurityConfig` (stateless JWT filter chain), `JpaConfig`, `WebConfig`, `OpenApiConfig`.

## Key Design Decisions

**Dual IDs**: Every entity has an internal `Long id` (database PK, used for joins) and a `UUID publicId` (exposed in the API). Expense endpoints use `publicId` in paths.

**User scoping**: All expense queries include `userId` to enforce ownership (e.g., `findByPublicIdAndUserId`). The `User` object from the JWT is passed through to services.

**No Lombok**: The codebase uses Java records for domain models and manual constructors elsewhere. Do not introduce Lombok.

**Checkstyle rules**: Based on Google Java Style. Key constraints: 4-space indentation (no tabs), max 120-char lines, no star imports, `UPPER_SNAKE_CASE` constants, braces required on all blocks, files must end with a newline.

## Database Migrations

Flyway migrations live in `src/main/resources/db/migration/`. Naming: `V{n}__{description}.sql`. The app runs with `ddl-auto: validate` — schema must match entities exactly, Flyway manages all DDL.

Integration tests bypass Flyway and use `ddl-auto: create-drop` via `@DynamicPropertySource`.

## API

App runs on `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`.

All `/api/expenses/**` endpoints require `Authorization: Bearer <token>`. Auth endpoints (`/api/auth/**`) are public.
