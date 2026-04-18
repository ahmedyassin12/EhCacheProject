# 📚 Book Management API – Spring Boot + JWT + Ehcache + Docker 
>Designed as a secure backend service for managing users and resources in a training-center-like environment.

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Ehcache](https://img.shields.io/badge/Cache-Ehcache%203-orange.svg)](https://www.ehcache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Live Demo](https://img.shields.io/badge/Live%20Demo-Swagger%20UI-success)](https://ehcacheproject.onrender.com/swagger-ui/index.html)
[![Tests](https://img.shields.io/badge/Tests-100%2B-brightgreen.svg)]()

A **production-style Book Management API** built with Spring Boot, demonstrating secure authentication, in-process caching with Ehcache 3, role-based access control, and clean layered architecture — fully Dockerized for easy local deployment.

> ✅ **Live Swagger UI**: [https://ehcacheproject.onrender.com/swagger-ui/index.html](https://ehcacheproject.onrender.com/swagger-ui/index.html)
> *(Free tier — may cold start after inactivity. Wait ~30 seconds if a 503 appears.)*

---

## ⭐ Key Highlights

- 🔐 Stateless JWT authentication with **access + refresh token rotation**
- 📧 **Email verification** required before account activation
- 🔑 **Password reset** flow via email token
- ⚡ **Ehcache 3 in-process caching** — TTL, `@CacheEvict`, `@CachePut`, `@Cacheable`
- 🛡️ Role-based access control — `SUPERADMIN` and `ADMIN` with distinct permissions
- ✅ Input validation via custom annotations + Jakarta Bean Validation
- 🌍 Centralized exception handling via `@RestControllerAdvice`
- 🐳 Fully **Dockerized** — one command starts everything
- 🧪 **100+ tests** — JUnit 5 + Mockito unit & integration + Postman
- 📄 Fully documented via **Swagger (OpenAPI 3)**

---
  
  ## 🏗️ Architecture
  
  ```
┌─────────────────────────────────────────────────────────────┐
│                 Client (Postman / Swagger UI)               │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP (JWT in Header)
┌───────────────────────────▼─────────────────────────────────┐
│              Security Filter (OncePerRequestFilter)         │
│    Validates JWT & Checks Blacklist (1. Cache -> 2. DB)     │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│               Controller Layer (@RestController)            │
│          Handles HTTP, delegates to services, @Valid        │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                  Service Layer (@Service)                   │
│      Business logic, @Cacheable, Token Revocation Logic     │
└──────────────┬────────────────────────────┬─────────────────┘
               │                            │
┌──────────────▼──────────┐      ┌──────────▼─────────────────┐
│  Repository Layer (JPA) │      │   Ehcache 3 (In-Process)   │
│  PostgreSQL via Hibernate◄─────┤  Blacklist Mirror (7d TTL) │
│ (Users, Books, Blacklist)│      │  User/Book Cache (Heap)    │
└──────────────┬──────────┘      └────────────────────────────┘
               │
┌──────────────▼──────────┐
│   PostgreSQL Database   │
│  The "Source of Truth"  │
└─────────────────────────┘
  ```

### Layer Responsibilities

**Controller Layer** — Receives HTTP requests, validates input via `@Valid`, delegates to services, returns structured responses.

**Service Layer** — Contains all business logic: authentication flows, token rotation, cache management, and book CRUD operations. Uses DTOs to decouple internal entities from external API responses.

**Repository Layer** — Spring Data JPA repositories with custom JPQL queries for role-scoped data access.

**Security Layer** — JWT filter chain via `OncePerRequestFilter`, role-based endpoint authorization, and Implements a Hybrid Blacklist Strategy: tokens are persisted in PostgreSQL for durability and mirrored in Ehcache (7-day TTL) for O(1) lookup performance. This covers manual logouts and proactive access token revocation during rotation..

**Cache Layer** — Ehcache 3 in-process cache running inside the JVM. Ideal for single-instance deployments — zero external dependencies, no network overhead.

---

## ⚡ Ehcache 3 Caching Strategy

This project deliberately uses **Ehcache 3** (in-process) rather than Redis (distributed) — a conscious architectural decision suited to single-instance deployments where external infrastructure overhead is unnecessary.

### What is Cached

|  Cache Name |             Content             |   TTL  | TTI |      Eviction Trigger     |
|      ---    |              ---                |   ---  | --- |            ---            |
| `JwtTokens` |  Revoked Access/Refresh Tokens  | 7 days |  —  | Manual addition on Logout/Auth/Refresh |
|   `users`   |       User objects by ID        | 1 day  |  _  | Profile update, deletion  |
|  `allUsers` |       Full user list            | 1 day  |  —  | Any user add/update/delete|
|   `books`   |       Book objects by ID        | 1 day  |  —  | Book update, deletion     |
|  `allbooks` |       full books list           | 1 day  |  —  | Book add/update, delete   |

### Cache Invalidation Strategy

Cache invalidation is handled using a combination of **Spring Cache annotations** and **manual eviction** for security-critical flows. This ensures data consistency while maintaining high performance.

```java
// 🔐 Token invalidation on logout (manual eviction for full control)
public void logout(String token) {
    // Remove all user tokens from cache
    tokens.forEach(t ->
        cacheManager.getCache("JwtTokens").evict(t.getToken())
    );

    // Persist revocation in database
    tokenDAO.revokeAllTokensByUser(user.getId());
}


// 👤 Update user → refresh single user cache + invalidate user list cache
@Caching(
    put = {
        @CachePut(cacheNames = "userDtos", key = "#result.id")
    },
    evict = {
        @CacheEvict(cacheNames = "AlluserDtos", allEntries = true)
    }
)
public UserDto updateUser(UpdateRequest request) { ... }


// ❌ Delete user → remove from individual cache + invalidate list cache
@Caching(evict = {
    @CacheEvict(cacheNames = "userDtos", key = "#id"),
    @CacheEvict(cacheNames = "AlluserDtos", allEntries = true)
})
public void deleteUser(Long id) { ... }


```

### TTL

- **TTL (Time-To-Live)** — entry expires X time after creation regardless of access. Used for security-sensitive data (tokens) where freshness is mandatory.

### Why Ehcache Over Redis Here

|            Concern            | Ehcache (this project) |     Redis   |
|             ---               |            ---         |      ---    |
|     External service needed   |         ❌ No         |    ✅ Yes   |
|         Network overhead      |         ❌ None       | ✅ Per call |
|         Single instance       |      ✅ Perfect fit   |   Overkill  |
|  Multi-instance / distributed |      ❌ Not suitable  | ✅ Required |
|       Setup complexity        |      Minimal           | Significant |

> For distributed deployments across multiple instances, Redis would be the correct choice — as demonstrated in my [E-Learning project](https://github.com/ahmedyassin12/E-LearningAPP).

---

## 🔐 Security

- Stateless JWT with **access token (2 min) + refresh token rotation**
- `OncePerRequestFilter` validates every request before it reaches controllers
- Proactive Token Blacklisting: Implements a strict revocation policy to prevent session hijacking:
    Logout: Both Access and Refresh tokens are immediately moved to the blacklist.
    Auth & Refresh Endpoints: The previous Access Token is revoked as soon as a new one is issued, ensuring only the most recent credential is valid.
    Persistence: All revoked tokens are stored in PostgreSQL (Source of Truth) and mirrored in Ehcache (High-speed check) with a 7-day TTL.
    - Email verification required before account activation
    - Strong password enforced via custom `@StrongPassword` annotation
    - Role-based authorization — `SUPERADMIN` vs `ADMIN` permission scoping

---

## 👥 Role-Based Access Control

| Role | Permissions |
|---|---|
| `SUPERADMIN` | Full access to all endpoints including admin management |
| `ADMIN` | Full book CRUD + user management — cannot create or delete other admins |

---

## 📧 Auth Flows

### Registration & Verification
```
POST /api/v1/auth/register
      ↓
Account created (disabled)
      ↓
Verification token generated (UUID, 30 min expiry)
      ↓
Verification email sent
      ↓
GET /api/v1/auth/verify?token=...
      ↓
Account activated ✅ → login now available
```

### Password Reset
```
POST /api/v1/auth/forgot-password  (email submitted)
      ↓
Reset token generated + emailed
      ↓
POST /api/v1/auth/reset-password?token=...
      ↓
Password updated ✅
```

---

## ✅ Validation & Error Handling

Custom validation layer collects **all** violations before returning — not just the first one:

```json
{
  "errors": [
    "Password must contain at least one uppercase letter",
    "Username contains invalid characters",
    "Email format is invalid"
  ]
}
```

Custom annotations implemented:
- `@StrongPassword` — minimum 8 chars, uppercase, lowercase, digit, special character
- `@UsernameValidator` — unique username, no special character injection
- `@ValidRole` — strictly validates against `ADMIN` / `SUPERADMIN` enum

---

## 🧪 Testing

100+ test cases with 80% line coverage.

|      Layer       |         Framework          |                 What's Tested                      |
|       ---        |            ---             |                      ---                           |
|  Service Layer   |     JUnit 5 + Mockito      |           Business logic, cache eviction           |
| Repository Layer |   JUnit 5 + `@DataJpaTest` |               Custom JPQL queries                  |
| Controller Layer | JUnit 5 + `@SpringBootTest`|                 important EndPoints                |
|       API        |           Postman          | All endpoints — auth flows, book CRUD, edge cases  |

```bash
./gradlew test
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Spring Boot 3.1** | REST API framework |
| **Spring Security + JWT** | Authentication & authorization |
| **Spring Data JPA (Hibernate)** | ORM & database access |
| **PostgreSQL 15** | Relational database |
| **Ehcache 3** | In-process caching (TTL ) |
| **Docker & Docker Compose** | Containerized local environment |
| **Swagger (OpenAPI 3)** | API documentation |
| **JUnit 5 + Mockito** | Unit & integration testing |
| **Gradle** | Build tool |

---

## 💡 Design Decisions

**Why Ehcache over Redis?**
This is a single-instance application with no distributed deployment requirement. Ehcache runs inside the JVM — zero network overhead, zero external dependencies, zero infrastructure cost. Redis would add unnecessary complexity for a single-instance use case. For distributed caching across multiple instances, see my [E-Learning project](https://github.com/ahmedyassin12/E-LearningAPP) which uses Redis.

**Why short access token expiry (2 minutes)?**
Short-lived access tokens minimize the window of exposure if a token is intercepted. The refresh token rotation ensures seamless UX — the client transparently gets a new access token without forcing re-login.

**Why collect all validation errors instead of failing fast?**
Returning all violations at once gives API consumers a complete picture of what needs fixing. Failing on the first error forces multiple round trips for multi-field forms — poor developer experience.

**Why a Hybrid Blacklist (DB + Cache)???**
We prioritize both Persistence and Performance. Using a Database ensures that blacklisted tokens stay invalid even after a server restart. Using Ehcache as a mirror allows our SecurityFilter to reject malicious requests in microseconds without overloading the database.

Why 7-Day TTL for the Blacklist Cache?
Since the Refresh Token has a maximum life of 7 days, any token (Access or Refresh) added to the blacklist must remain there for the full duration of that potential window. This prevents a "ghost" token from being reused if the cache were to expire before the token's internal cryptographic expiration..
---

## ⚙️ Setup & Run

### 🔑 Environment Variables

```properties
# EmailVerification 
EMAIL_Username=your_brevo_username
EMAIL_PASSWORD=your_Brevo_password
EMAIL_Sender=your_email@gmail.com

```

### 🐳 Docker (Recommended — one command)

```bash
git clone https://github.com/ahmedyassin12/EhCacheProject.git
cd EhCacheProject
docker-compose up --build
```

This starts:
- Spring Boot API on `http://localhost:9099`
- PostgreSQL on port `5433`

Access Swagger: [http://localhost:9099/swagger-ui.html](http://localhost:9099/swagger-ui.html)

### ▶️ Manual Run

```bash
./gradlew clean build
./gradlew bootRun
```

---



> ⚠️ Never commit secrets. Use `.env` files locally.

---

## 📡 API Documentation

All endpoints available via Swagger:

👉 [https://ehcacheproject.onrender.com/swagger-ui/index.html](https://ehcacheproject.onrender.com/swagger-ui/index.html)

**Quick start:**
1. `POST /api/v1/auth/register` — create account
2. Verify email via link received
3. `POST /api/v1/auth/authenticate` — get access + refresh tokens
4. Use access token as `Bearer` in all secured endpoints
5. `POST /api/v1/auth/refresh-token` — get new access token when expired
6. `POST /api/v1/auth/logout` — invalidate tokens (Postman only)

---

## 📌 Future Improvements

- Implement cursor-based pagination
- Introduce rate limiting (Bucket4j)
- Add centralized logging (ELK)

---

## 👤 Author

**Ahmed Yassine Zouaoui**
Backend Developer — Java / Spring Boot

---

## 📝 License

MIT License
