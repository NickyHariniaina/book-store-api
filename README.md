<picture src="https://img.shields.io/badge/Java-21-%23ED8B00?logo=openjdk&logoColor=white" />
<picture src="https://img.shields.io/badge/Spring%20Boot-3.2.2-%236DB33F?logo=springboot&logoColor=white" />
<picture src="https://img.shields.io/badge/PostgreSQL-13.9-%234169E1?logo=postgresql&logoColor=white" />
<picture src="https://img.shields.io/badge/Gradle-8-%2302303A?logo=gradle&logoColor=white" />
<picture src="https://img.shields.io/badge/Flyway-9.22.3-%23CC0200?logo=flyway&logoColor=white" />
<picture src="https://img.shields.io/badge/coverage-80%25-brightgreen?logo=javacoco" />
<picture src="https://img.shields.io/badge/license-MIT-blue" />

<br />

<div align="center">
  <h1>Bookstore API</h1>
  <p><strong>A comprehensive REST API for bookstore catalog management, inventory tracking, and point-of-sale operations.</strong></p>
  <p>Built with Spring Boot 3.2 — modern, layered, and production-ready.</p>
</div>

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
  - [Health & Monitoring](#health--monitoring)
  - [Books](#books)
  - [Authors](#authors)
  - [Genres](#genres)
  - [Publishers](#publishers)
  - [Book Editions](#book-editions)
  - [Price History](#price-history)
  - [Inventory](#inventory)
  - [Sales](#sales)
  - [Customers](#customers)
- [Database Schema](#database-schema)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [CI/CD](#cicd)
- [Project Structure](#project-structure)

---

## Overview

The **Bookstore API** powers digitalization for bookstore operations. It provides a complete backend for:

- **Catalog Management** — Books, authors, genres, publishers, and editions
- **Inventory Tracking** — Stock management with movement audit trail
- **Point of Sale** — Create, confirm, cancel, and refund sales with automatic stock adjustments
- **Price Management** — Time-series pricing with history tracking
- **Customer Management** — Customer profiles and sales history

The project follows a **single-store model** with a layered architecture and comprehensive testing at all levels.

---

## Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.2.2 |
| **Build Tool** | Gradle | 8.x (wrapper) |
| **ORM** | Spring Data JPA / Hibernate | 6.4.3 |
| **Database** | PostgreSQL | 13.9+ |
| **Migrations** | Flyway | 9.22.3 |
| **API Documentation** | OpenAPI 3.0.3 + Springdoc | 2.5.0 |
| **Security** | Spring Security + BCrypt | — |
| **Validation** | Jakarta Bean Validation | — |
| **Code Quality** | Lombok, google-java-format, JaCoCo (80% min) | — |
| **Testing** | JUnit 5, Mockito, Testcontainers, MockMvc, WebTestClient | — |
| **Cloud** | AWS Lambda, SQS, EventBridge, SES, S3 | — |

---

## Architecture

The application follows a clean **layered architecture**:

```
HTTP Request
     │
     ▼
┌─────────────────┐
│   Controllers   │  ← REST endpoints, HTTP handling
├─────────────────┤
│    Mappers      │  ← Entity ↔ DTO conversion
├─────────────────┤
│    Services     │  ← Business logic, @Transactional
├─────────────────┤
│  Repositories   │  ← Data access (Spring Data JPA)
├─────────────────┤
│     Entities    │  ← JPA entities, Flyway-managed tables
└─────────────────┘
     │
     ▼
   PostgreSQL
```

**Cross-cutting concerns:**
- **GlobalExceptionHandler** — Unified error handling via `@RestControllerAdvice`
- **SecurityConf** — Spring Security filter chain
- **Optimistic Locking** — Inventory items use `@Version` for concurrency control

---

## Getting Started

### Prerequisites

- **Java 21** (Corretto or OpenJDK)
- **PostgreSQL 13.9+**
- **Gradle 8.x** (or use the included `./gradlew` wrapper)

### Configuration

The application requires the following environment variables:

| Variable | Required | Description |
|----------|----------|-------------|
| `SPRING_DATASOURCE_URL` | ✅ Yes | JDBC URL for PostgreSQL (e.g., `jdbc:postgresql://localhost:5432/bookstore`) |
| `SPRING_DATASOURCE_USERNAME` | ✅ Yes | Database username |
| `SPRING_DATASOURCE_PASSWORD` | ✅ Yes | Database password |
| `AWS_EVENT_BRIDGE_BUS` | ❌ No | EventBridge bus name (default: `default-bus`) |

Additional AWS configuration may be needed for SES, S3, and SQS features.

### Running the Application

```bash
# Clone the repository
git clone <repo-url>
cd book-store-api

# Run tests
./gradlew test

# Start the application
./gradlew bootRun

# Build the project
./gradlew build

# Format code (google-java-format)
./format.sh
```

> **Note:** Flyway migrations run automatically on startup, creating all required tables.

---

## API Endpoints

### Health & Monitoring

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/ping` | Health check — returns `"pong"` |
| `GET` | `/health/db` | Database connectivity check |
| `GET` | `/health/email?to=` | Sends test emails via SES |
| `GET` | `/health/event1` | Fires test EventBridge events |
| `POST` | `/health/event/uuids` | Verifies async event processing |

### Books

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/books?page=&size=&sortBy=&sortDir=` | List/search books (paginated) |
| `GET` | `/books/{id}` | Get full book detail (includes authors, genres) |
| `POST` | `/books` | Create a new book |
| `PUT` | `/books/{id}` | Update a book |
| `DELETE` | `/books/{id}` | Delete a book (cascades to editions) |
| `POST` | `/books/{id}/authors/{authorId}` | Add an author to a book |
| `DELETE` | `/books/{id}/authors/{authorId}` | Remove an author from a book |
| `POST` | `/books/{id}/genres/{genreId}` | Assign a genre to a book |
| `DELETE` | `/books/{id}/genres/{genreId}` | Remove a genre from a book |
| `GET` | `/books/{bookId}/stock` | Get total stock across all editions |
| `GET` | `/books/low-stock` | Get all items with stock at or below reorder level |

### Authors

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/authors?page=&size=&sortBy=&sortDir=` | List/search authors (paginated) |
| `GET` | `/authors/{id}` | Get author details |
| `POST` | `/authors` | Create a new author |
| `PUT` | `/authors/{id}` | Update an author |
| `DELETE` | `/authors/{id}` | Delete an author |

### Genres

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/genres?page=&size=&sortBy=&sortDir=` | List all genres (paginated) |
| `GET` | `/genres/{id}/books` | Get books in a genre (paginated) |
| `POST` | `/genres` | Create a new genre |
| `PATCH` | `/genres/{id}/rename` | Rename a genre |
| `DELETE` | `/genres/{id}` | Delete a genre |
| `GET` | `/genres/revenue` | Revenue breakdown per genre (dashboard) |

### Publishers

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/publishers?page=&size=&sortBy=&sortDir=` | List/search publishers (paginated) |
| `GET` | `/publishers/{id}` | Get publisher details |
| `POST` | `/publishers` | Create a new publisher |
| `PUT` | `/publishers/{id}` | Update a publisher |
| `DELETE` | `/publishers/{id}` | Delete a publisher |

### Book Editions

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/books/{bookId}/editions` | List editions for a book |
| `GET` | `/editions/{id}` | Get edition details |
| `GET` | `/editions/isbn/{isbn}` | Lookup edition by ISBN |
| `POST` | `/books/{bookId}/editions` | Create edition under a book |
| `PUT` | `/editions/{id}` | Update an edition |
| `PATCH` | `/editions/{id}/activate` | Activate edition for sale |
| `PATCH` | `/editions/{id}/deactivate` | Deactivate edition |

### Price History

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/editions/{id}/prices` | List all price records for an edition |
| `GET` | `/editions/{id}/prices/current` | Get current active price |
| `POST` | `/editions/{id}/prices` | Set new price (closes previous current) |

### Inventory

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/inventory/arrival` | Record book arrival (+stock) |
| `POST` | `/inventory/adjustment` | Manual stock adjustment (+/-) |
| `POST` | `/inventory/damaged` | Mark copies as damaged (-stock) |
| `POST` | `/inventory/lost` | Mark copies as lost (-stock) |
| `GET` | `/inventory/low-stock` | Get all low-stock items |
| `GET` | `/editions/{editionId}/stock` | Get stock quantity for an edition |
| `GET` | `/editions/{editionId}/movements` | Inventory movement history for an edition |

### Sales

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/sales` | Create a new sale with items |
| `GET` | `/sales?page=&size=` | List all sales (paginated) |
| `GET` | `/sales/{id}` | Get sale details |
| `PATCH` | `/sales/{id}/confirm?paymentMethod=` | Confirm sale (deducts stock) |
| `PATCH` | `/sales/{id}/cancel` | Cancel a pending sale |
| `PATCH` | `/sales/{id}/refund` | Refund a paid sale (restores stock) |

### Customers

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/customers` | List all customers |
| `GET` | `/customers/{id}` | Get customer details |
| `POST` | `/customers` | Create a new customer |
| `PUT` | `/customers/{id}` | Update customer information |
| `GET` | `/customers/{id}/sales` | Get a customer's sales history |

---

## Database Schema

The database is managed via **Flyway migrations** (19 files) and consists of the following core tables:

```
book ──┬── book_genre ──── genre
       │
       ├── book_author ──── author
       │
       └── book_edition ──┬── publisher
                          │
                          ├── book_price_history
                          │
                          ├── inventory_item
                          ├── inventory_movement
                          │
                          └── sale_item ── sale ── customer
```

| Table | Description |
|-------|-------------|
| `book` | Core book entity (title, summary, language, cover) |
| `genre` | Book categories (unique name) |
| `book_genre` | Many-to-many: book ↔ genre |
| `author` | Author profiles |
| `book_author` | Many-to-many: book ↔ author (with role & contribution order) |
| `publisher` | Publisher information |
| `book_edition` | Specific edition of a book (ISBN, format, active flag) |
| `book_price_history` | Time-series pricing (effective_from, effective_to) |
| `inventory_item` | Stock tracking (optimistic locking via `@Version`) |
| `inventory_movement` | Audit trail for all stock changes (arrival, sale, adjustment, etc.) |
| `customer` | Customer profiles (email, phone, password, role) |
| `sale` | Point-of-sale transactions (status, payment method) |
| `sale_item` | Line items within a sale |

**Enums used throughout:**

| Enum | Values |
|------|--------|
| `BookFormat` | `HARDCOVER`, `PAPERBACK` |
| `BookLanguage` | `MALAGASY`, `FRANCAIS`, `ENGLISH`, `ESPANOL` |
| `AuthorRole` | `AUTHOR`, `CO_AUTHOR`, `EDITOR`, `TRANSLATOR`, `ILLUSTRATOR`, `COMPILER` |
| `SaleStatus` | `PENDING`, `PAID`, `CANCELLED`, `REFUNDED` |
| `PaymentMethod` | `CASH`, `CARD`, `MOBILE_MONEY`, `BANK_TRANSFER` |
| `InventoryMovementType` | `ARRIVAL`, `SALE`, `ADJUSTMENT`, `DAMAGED`, `LOST` |

---

## Error Handling

All errors are handled centrally via `GlobalExceptionHandler` (`@RestControllerAdvice`) and return a consistent JSON response:

```json
{
  "type": "ERROR_TYPE",
  "message": "Human-readable description"
}
```

| HTTP Status | When |
|-------------|------|
| `400 Bad Request` | Invalid input, missing parameters, type mismatch |
| `401 Unauthorized` | Authentication failure |
| `403 Forbidden` | Access denied |
| `404 Not Found` | Resource not found |
| `409 Conflict` | Data integrity violation, duplicate entry |
| `429 Too Many Requests` | Rate limit or DB deadlock detected |
| `500 Internal Server Error` | Unexpected server error |
| `501 Not Implemented` | Feature not yet implemented |

---

## Testing

The project enforces **minimum 80% line coverage** via JaCoCo and employs a three-tier testing strategy:

### Unit Tests — Service Layer
- **Framework:** JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`)
- **Scope:** Business logic in isolation
- **Examples:** `BookServiceTest`, `SaleServiceTest`, `InventoryServiceTest`

### Controller Tests — Web Layer
- **Framework:** `@WebMvcTest` + MockMvc
- **Scope:** Request mapping, validation, response serialization
- **Examples:** `BookControllerTest`, `GenreControllerTest`, `SaleControllerTest`

### Integration Tests — Full Stack
- **Framework:** Testcontainers (PostgreSQL 13.9) + `WebTestClient`
- **Scope:** End-to-end flows with real database
- **Examples:** `GenreIT`, `AuthorIT`, `SaleIT`, `InventoryIT`

```bash
# Run all tests with coverage
./gradlew test

# Check coverage only
./gradlew jacocoTestCoverageVerification

# Generate coverage report
./gradlew jacocoTestReport
# → Report available at build/reports/jacoco/test/html/index.html
```

---

## CI/CD

### Continuous Integration (`.github/workflows/ci.yml`)
- Triggers on **push** and **pull_request** to any branch
- Runs full test suite with **JDK 21 (Corretto)**
- Validates code formatting via `google-java-format`

### Continuous Delivery (`.github/workflows/cd-compute.yml`)
- Triggers on push to **preprod** or **prod** branches
- Builds via **AWS SAM**, zips artifacts, uploads to S3
- Uses **Poja API** for deployment orchestration

### Release Versioning (`.github/workflows/release-version.yml`)
- Automatic version bump via **conventional-changelog**
- Creates git tags automatically

---

## Project Structure

```
src/
├── main/
│   ├── java/com/onlydevs/bookstore/
│   │   ├── PojaApplication.java                 # Application entry point
│   │   ├── endpoint/rest/
│   │   │   ├── controller/                      # REST controllers (10)
│   │   │   ├── mapper/                          # Entity ↔ DTO mappers (9)
│   │   │   └── security/                        # Spring Security config
│   │   ├── model/
│   │   │   ├── *.java                           # JPA entities (13)
│   │   │   ├── dto/request/                     # Request DTOs (14)
│   │   │   ├── dto/response/                    # Response DTOs (13)
│   │   │   ├── enums/                           # Enum types (6)
│   │   │   └── exception/                       # Custom exceptions (7)
│   │   ├── repository/                          # JPA repositories (13)
│   │   └── service/                             # Business logic (9)
│   └── resources/
│       ├── application.properties
│       └── db/migration/                        # Flyway migrations (19)
└── test/
    └── java/com/onlydevs/bookstore/
        ├── conf/                                # Test configuration
        ├── endpoint/rest/controller/            # Controller tests (8)
        ├── endpoint/rest/mapper/                # Mapper tests (7)
        ├── integration/                         # Integration tests (5)
        └── service/                             # Service unit tests (9)
```

---

## OpenAPI Specification

A comprehensive **OpenAPI 3.0.3** specification is available at:

```
doc/api.yml
```

It covers all endpoints, request/response schemas, enums, and error responses. A static Swagger HTML page is also bundled at `src/main/resources/static/swagger.html`.

> **Note:** Swagger UI is currently disabled in application properties. To enable it, set `springdoc.swagger-ui.enabled=true`.

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <sub>Built with ❤️ using <a href="https://spring.io/projects/spring-boot">Spring Boot</a> and <a href="https://github.com/hei-school/poja">POJA</a></sub>
</div>
