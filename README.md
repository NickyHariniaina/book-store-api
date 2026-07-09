# Bookstore API

REST API for bookstore digitalization — catalog management, inventory tracking, and point-of-sale operations. 📚✨
Because even bookstores deserve a little tech magic.

![Java 21](https://img.shields.io/badge/Java-21-%23ED8B00)
![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2.2-%236DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13.9-%234169E1)
![Flyway](https://img.shields.io/badge/Flyway-9.22.3-red)
![Coverage](https://img.shields.io/badge/coverage-80%25-brightgreen)

---

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.2.2 |
| PostgreSQL | 13.9+ |
| Flyway | 9.22.3 |
| Gradle | 8.x |
| Testcontainers / JUnit 5 / Mockito | — |
| JaCoCo (80% min) | 0.8.11 |

---

## Getting Started

### Prerequisites

- Java 21 _(your JVM will cry otherwise)_
- PostgreSQL 13.9+ _(your data will flee otherwise)_

### Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `SPRING_DATASOURCE_URL` | Yes | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Yes | DB username |
| `SPRING_DATASOURCE_PASSWORD` | Yes | DB password |

### Commands

```bash
./gradlew bootRun        # Summon the app into existence
./gradlew test           # Make the tests happy (or sad)
./gradlew build          # Build it like LEGO
./format.sh              # Make the code pretty
```

---

## API Documentation

All endpoints are documented and testable via Swagger UI — the interactive playground where you can poke, prod, and pet every endpoint without writing a single `curl` command. Swagger also exposes the raw spec at `/v3/api-docs`.

| Environment | URL |
|-------------|-----|
| **Local development** | <http://localhost:8080/swagger.html> |
| **Production** | `https://your-domain.com/swagger.html` |

Covers everything: books, authors, genres, publishers, editions, inventory, sales, and customers — with request/response schemas and live "Try it out" buttons.

---

## Testing

```bash
./gradlew test                    # Tests with coverage (min 80%)
./gradlew jacocoTestReport        # Fancy HTML report → build/reports/jacoco/test/html/
```

We test in **3 layers** — like a cake, but with more assertions:

| Layer | Tool | Vibe |
|-------|------|------|
| **Unit** | JUnit 5 + Mockito | Services doing their thing in isolation |
| **Controller** | MockMvc | Endpoints behaving in public |
| **Integration** | Testcontainers + WebTestClient | The full symphony with a real Postgres |

---

## Project Structure

```
src/main/java/com/onlydevs/bookstore/
├── PojaApplication.java          # Where it all begins
├── endpoint/
│   ├── rest/
│   │   ├── controller/           # 14 REST controllers saying hello
│   │   ├── controller/health/    # Are we alive? Let's check.
│   │   ├── mapper/               # 9 Entity ↔ DTO translators
│   │   ├── security/             # Who goes there?
│   │   └── model/                # Error response DTO
│   └── event/                    # Async gossips (SQS/EventBridge)
├── model/
│   ├── *.java                    # 12 JPA entities living their best life
│   ├── dto/request/              # 18 request DTOs
│   ├── dto/response/             # 15 response DTOs
│   ├── enums/                    # 6 enums — choices matter
│   └── exception/                # 7 ways things can go wrong
├── repository/                   # 13 JPA repositories, data's best friend
├── service/                      # 9 business + 2 event services
├── concurrency/                  # Multitasking pros
├── datastructure/                # For when lists need a pep talk
├── file/hash/ & file/zip/        # File whisperers
├── handler/                      # AWS Lambda handlers
└── mail/                         # Email carrier pigeons (SES)
```

---

## One more thing...

```bash
./gradlew bootRun --args='--server.port=9090'  # When 8080 is just too mainstream
./gradlew test --tests "*BookServiceTest"       # Test ONE thing, you glorious rebel
```

---

<div align="center">
  <sub>Built with ☕, ❤️, and questionable commit messages — <code>com.onlydevs.bookstore</code></sub>
</div>
