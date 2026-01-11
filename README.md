# EhCacheProject
# 📚 Book Management API (Spring Boot + JWT + Swagger + Docker)

A secure and performant Spring Boot application for managing books. Features include JWT authentication, refresh tokens, email verification, password reset, validation with custom annotations, global exception handling, and Swagger API documentation — all Dockerized for easy deployment.

---

## 🚀 Features

- ✅ User Registration & Login (JWT + Refresh Token)
- ✉️ Email Verification & Password Reset via Mail
- 📚 Book Management (CRUD)
- 🧠 Caching with Ehcache(caching my GetToken(setting Up cacheEvict Perfectly) , getAllUser+getUser(setting up cachePut and cacheEvict ), 
- 🔐 Secure Endpoints using Spring Security : Security Architecture is Stateless authentication using JWT + Refresh Token rotation.
- ⚙️ Global Exception Handling
- 🧪 Input Validation with:
  - DTOs:
  - Custom Annotations : for complex business rules (e.g., unique username constraints).
  - Domain-Driven Design (DDD) Principles: Separation of concerns using DTOs, Service interfaces, and a Global Exception Handling layer.
- 🔍 Swagger/OpenAPI UI for exploring endpoints
- 🐳 Docker + Docker Compose for easy deployment
---
🚀 Performance & Scalability Optimization
Latency Reduction: Leveraged Ehcache 3 to implement a high-performance local caching layer, reducing API response times by up to 90% for read-heavy endpoints.

Database Offloading: Minimized expensive PostgreSQL I/O operations by caching frequently accessed entities, ensuring system stability during traffic spikes.

Advanced Cache Management: * Optimized the Cache-Aside pattern using @Cacheable.

Implemented precise Cache Invalidation strategies with @CacheEvict to prevent stale data in authentication and book-management flows.

Configured TTL (Time-To-Live) and TTI (Time-To-Idle) policies to balance memory usage and data freshness.

---
##🛠 Tech Stack
-Backend: Java 17, Spring Boot 3, Spring Security

-Database: PostgreSQL (Relational)

-Caching: Ehcache 3 making most used EndPoints so fast for scalability.

-Documentation: Swagger UI / OpenAPI 3.0

-DevOps: Docker, Docker Compose

Testing: JUnit 5, Mockito (80% Line Coverage), Postman 

## 🔗 Live API Docs

You can explore and test all available endpoints using Swagger UI:  
👉 [**Swagger UI**](https://ehcacheproject.onrender.com/swagger-ui/index.html)
(Note: As this is hosted on Render's free tier, the first request may take ~30 seconds to wake up.)

------

🧪 Testing & Quality Assurance
Quality is prioritized with 80% code coverage. Tests cover security filters, service business logic, and cache eviction.

------
## 🧑‍💻 How to Use the API

### 1. 📝 **Register**

- Use the `/api/v1/auth/register` endpoint to register a new user.
- Make sure to enter a **valid email** — this will be used for verification.

### 2. 📧 **Email Verification Required**

- After registration, check your email inbox for a **verification link**.
- Click it to verify your account.
- **You cannot log in or use secured endpoints until your email is verified.**

### 3. 🔐 **Authenticate**

- Once verified, use the `/api/v1/auth/authenticate` endpoint to log in.
- On successful login, you'll receive:
  - ✅ **Access Token** (valid for **2 minutes**)
  - 🔁 **Refresh Token**

### 4. 🔄 **Token Refresh**

- If your access token expires after 2 minutes, use `https://ehcacheproject.onrender.com/api/v1/auth/refresh-token` with your refresh token to get a new access token.

### 5. 🚪 **Logout**

- Use the `https://ehcacheproject.onrender.com/api/v1/auth/logout` endpoint to log out and invalidate your tokens.
- ❗ This endpoint is available **only via Postman** or external tools — it's not accessible from Swagger UI.

---

## 🛡️ Role-Based Access

There are **two roles** in this application:

| Role        | Permissions |
|-------------|-------------|
| `SUPERADMIN` | ✅ Full access to all endpoints |
| `ADMIN`      | ✅ Access to everything except:<br>❌ Cannot create new admins<br>❌ Cannot delete existing admins |

Role assignment is done during user creation (in the `register` request payload).

## 🛡️ Data Integrity && ✅ Validation Rules

##During registration and authentication, the following validations are enforced:

To ensure system reliability and security, the application enforces strict validation at the controller and service layers. Any violation results in a standardized 400 Bad Request response with descriptive error messages.

-User Authentication Constraints
Email: Must adhere to RFC 5322 standards (valid email format).

Password Complexity: Enforced "Strong Password" policy:

Minimum 8 characters.

Mandatory inclusion of Uppercase, Lowercase, Digit, and Special Character.

Username: Must be unique; sanitized to prevent special character injection.

RBAC Validation: The role attribute is strictly validated against the defined Enum (ADMIN, SUPERADMIN).

-Entity Persistence (Books)
Strict Null/Empty Check: All book attributes (Title, Author, ISBN, etc.) are mandatory.

Update Policy: Partial updates are validated to ensure no field is cleared or set to null, maintaining data consistency in the PostgreSQL layer.


## 💡 Notes

💤 Note: This app is hosted on Render's free tier. If it hasn’t been used for a while, the backend may take 20–30 seconds to wake up on first request.
If Swagger doesn’t load at first, please refresh the page.
- This is a backend-only project — there is **no frontend**.
- You can interact with the API directly via Swagger or tools like **Postman**.
- Swagger may fail on deployed environments due to CORS — use Postman if needed.

  
--------------------
# IF  U WANT TO RUN IT LOCALLY : 
## 📦 Run with Docker

###1. 1.docker-compose up -d : starts ur containers first ...

2. Build the Application 
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
👤  Ahmed Yassin (Trao360)  ⚙️ Backend Developer | BI Enthusiast


📧 Reach out or open an issue to collaborate.
⭐ Star the repo if you like it!
📝 License This project is licensed under the MIT License.



