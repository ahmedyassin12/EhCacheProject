# EhCacheProject
# 📚 Book Management API (Spring Boot + JWT + Swagger + Docker)

A secure and performant Spring Boot application for managing books. Features include JWT authentication, refresh tokens, email verification, password reset, validation with custom annotations, global exception handling, and Swagger API documentation — all Dockerized for easy deployment.

---

## 🚀 Features

- ✅ User Registration & Login (JWT + Refresh Token)
- ✉️ Email Verification & Password Reset via Mail
- 📚 Book Management (CRUD)
- 🧠 Caching with Ehcache
- 🔐 Secure Endpoints using Spring Security
- ⚙️ Global Exception Handling
- 🧪 Input Validation with:
  - DTOs
  - Custom Annotations
  - Standard Java Validation
- 🔍 Swagger/OpenAPI UI for exploring endpoints
- 🐳 Docker + Docker Compose for easy deployment

---

## 🧱 Tech Stack

- Java 17
- Spring Boot 3
- Spring Security + JWT
- PostgreSQL (Dockerized)
- Ehcache
- Swagger (SpringDoc)
- Docker / Docker Compose

---

## 📦 Run with Docker

###1. 1. Build the Application 
./gradlew clean build : This will generate the .jar file in build/libs/.

### 2. Build the Docker Image

      bash:
      docker build -t spring/ehcache . 

### 3. Start the App with Docker Compose
      docker-compose up --build : This will: Start a PostgreSQL container then Launch your Spring Boot app on port 9099

 environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
      POSTGRES_DB: book
    ports:
      - "5433:5432"
      environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/book
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
          ports:
      - "9099:9090"  # Mapping host port 9099 to container port 9090
    networks:
      - spring-boot-network

🧪 Swagger Documentation
Swagger UI: http://localhost:9090/swagger-ui.html

OpenAPI JSON: http://localhost:9090/v3/api-docs


🧹 Clean Architecture Highlights
DTO Pattern – For cleaner request/response handling

Custom Annotations – For advanced validation scenarios

Global Exception Handling – Clean error responses

Validation Layer – Uses javax.validation + custom validators

🧪 Testing & Dev Tools

-Postman tested

-JUnit coverage for key services

-Swagger for exploring & testing endpoints

📬 Author
Ahmed Yassin (Trao360)
📧 Reach out or open an issue to collaborate.
⭐ Star the repo if you like it!


