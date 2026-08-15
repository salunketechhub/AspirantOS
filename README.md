# AspirantOS

> **"Plan. Study. Revise. Track."**  
> *UPSC Preparation Command Center*

---

## 1. Project Overview

**AspirantOS** is a specialized, full-stack web platform architected for Union Public Service Commission (UPSC) Civil Services examination aspirants. It provides a focused, distraction-free environment to structure preparation, organize subjects, track consistency, and analyze progress.

> **Current Development Status**:  
> * **Step 1 — Project Foundation** ✅ (Complete)  
> * **Step 2 — Authentication & User Management** ✅ (Complete)  
> * **Step 3 — UPSC Syllabus & Subject Architecture** ⏳ (Next)

---

## 2. Technology Stack

### Frontend
* **Framework**: Angular 20.3+ (Standalone Components, Signals, Router, functional Guards & Interceptors)
* **Language**: TypeScript 5.9+
* **Networking**: Angular `HttpClient` with `withFetch()` and `withInterceptors([authInterceptor])`
* **Styling**: Tailwind CSS 3.4+ & PostCSS
* **Typography**: Outfit & Plus Jakarta Sans

### Backend
* **Runtime**: Java 21 LTS
* **Framework**: Spring Boot 3.4.x
* **Security**: Spring Security 6 (Stateless JWT, BCrypt password hashing)
* **JWT**: JJWT 0.12.6 (HMAC-SHA256)
* **Data & Persistence**: Spring Data JPA, Hibernate, PostgreSQL Driver
* **Validation**: Jakarta Validation API
* **Documentation**: SpringDoc OpenAPI 2.8.5 / Swagger UI
* **Build System**: Maven 3.9+ with cross-platform wrapper (`mvnw`)

### Database
* **Database Engine**: PostgreSQL 15+ (Default port: `5432`)
* **Default Database Name**: `aspirantos`

---

## 3. Project Structure

```
AspirantOS/
├── .env.example              # Environment variables template (includes DB & JWT configs)
├── .gitignore                # Git ignore rules for root, frontend, and backend
├── README.md                 # Project documentation
│
├── database/                 # Database initialization and documentation
│   ├── init.sql              # Schema baseline, extensions, and users table
│   └── README.md             # PostgreSQL setup and troubleshooting guide
│
├── backend/                  # Spring Boot 3.x backend application
│   ├── pom.xml               # Maven configuration
│   ├── mvnw / mvnw.cmd       # Cross-platform Maven wrapper scripts
│   ├── .mvn/wrapper/         # Maven wrapper properties
│   └── src/
│       ├── main/
│       │   ├── java/com/aspirantos/
│       │   │   ├── AspirantOsApplication.java     # Application entry point
│       │   │   ├── config/
│       │   │   │   ├── CorsConfig.java            # CORS origin mapping
│       │   │   │   ├── SecurityConfig.java        # Spring Security filter chain & password encoder
│       │   │   │   └── OpenApiConfig.java         # Swagger OpenAPI specification with BearerAuth
│       │   │   ├── entity/
│       │   │   │   ├── Role.java                  # Role enum (USER, ADMIN)
│       │   │   │   └── User.java                  # JPA User entity (UserDetails)
│       │   │   ├── repository/
│       │   │   │   └── UserRepository.java        # Data access repository
│       │   │   ├── dto/
│       │   │   │   ├── auth/                      # RegisterRequest, LoginRequest, AuthResponse, UserResponse
│       │   │   │   └── common/                    # Standardized ErrorResponse
│       │   │   ├── security/
│       │   │   │   ├── JwtService.java            # JWT generation, extraction, & validation interface
│       │   │   │   ├── JwtServiceImpl.java        # HMAC-SHA256 token service implementation
│       │   │   │   ├── CustomUserDetailsService.java # UserDetailsService implementation
│       │   │   │   ├── JwtAuthenticationFilter.java  # Bearer token HTTP filter
│       │   │   │   └── JwtAuthenticationEntryPoint.java # 401 Unauthorized JSON handler
│       │   │   ├── service/
│       │   │   │   ├── HealthService.java         # Health service interface
│       │   │   │   ├── HealthServiceImpl.java     # Health service implementation
│       │   │   │   ├── AuthService.java           # Authentication service interface
│       │   │   │   └── AuthServiceImpl.java       # Registration, login, & profile service
│       │   │   ├── controller/
│       │   │   │   ├── HealthController.java      # /api/health and /api/health/db
│       │   │   │   └── AuthController.java        # /api/auth/register, /login, /me
│       │   │   └── exception/
│       │   │       ├── DuplicateEmailException.java
│       │   │       ├── InvalidCredentialsException.java
│       │   │       ├── ResourceNotFoundException.java
│       │   │       └── GlobalExceptionHandler.java# Centralized REST error handler
│       │   └── resources/
│       │       └── application.yml                # Configuration file
│       └── test/
│           └── java/com/aspirantos/
│               ├── controller/
│               │   ├── AuthControllerTest.java    # AuthController MockMvc tests
│               │   └── HealthControllerTest.java  # HealthController unit tests
│               ├── service/
│               │   └── AuthServiceTest.java       # AuthService unit & BCrypt tests
│               └── security/
│                   ├── JwtServiceTest.java        # JWT generation & expiry tests
│                   └── SecurityFilterChainTest.java # Security authorization integration tests
│
└── frontend/                 # Angular 20+ frontend application
    ├── package.json
    ├── angular.json
    ├── tailwind.config.js
    ├── postcss.config.js
    └── src/
        ├── index.html
        ├── main.ts
        ├── styles.css
        ├── environments/
        │   ├── environment.ts
        │   └── environment.development.ts
        └── app/
            ├── app.config.ts             # App providers (HttpClient + interceptors, Router)
            ├── app.routes.ts             # App routing table with authGuard
            ├── core/auth/
            │   ├── token-storage.service.ts # Dedicated storage abstraction for JWT
            │   ├── auth.models.ts        # TypeScript interfaces for auth payloads
            │   ├── auth.service.ts       # Signal-based authentication service
            │   ├── auth.guard.ts         # Functional route guard protecting /dashboard
            │   └── auth.interceptor.ts   # Functional HTTP interceptor adding Bearer token
            └── pages/
                ├── home/                 # Landing & health telemetry page
                ├── login/                # Sign In component with reactive validation
                ├── register/             # Registration component with password validation
                └── dashboard/            # Step 2 verification dashboard (protected)
```

---

## 4. Authentication Architecture & Security

### 4.1 Stateless JWT Authentication Flow
1. **Registration (`POST /api/auth/register`)**:
   - Accepts `firstName`, `lastName`, `email`, `password`.
   - Validates input, normalizes email to lowercase.
   - Rejects duplicate emails with `409 Conflict`.
   - Hashes password using **`BCryptPasswordEncoder` (strength 12)** before persisting.
   - Returns `201 Created` with safe `UserResponse`.
   - Angular redirects to `/login` with a success alert.

2. **Login (`POST /api/auth/login`)**:
   - Normalizes email, verifies credentials against Spring Security's `AuthenticationManager`.
   - Generates a signed **HMAC-SHA256 JWT** containing `sub` (email), `roles`, `iat`, and `exp`.
   - Returns `200 OK` with `accessToken`, `tokenType: "Bearer"`, and safe `user` profile.
   - Angular stores the token in `TokenStorageService` and updates reactive auth state.

3. **Protected Requests & State Restoration**:
   - `authInterceptor` attaches `Authorization: Bearer <token>` to protected `/api/...` requests.
   - `JwtAuthenticationFilter` validates signature and expiration on each request and populates the `SecurityContext`.
   - `GET /api/auth/me` returns the authenticated profile derived strictly from the server-side `SecurityContext`.
   - On unauthenticated access or token expiration, a `401 Unauthorized` response is caught by `authInterceptor`, clearing auth state and redirecting to `/login`.

4. **Security Hardening**:
   - **No Weak Fallback Secrets**: `JwtService` validates at startup that `JWT_SECRET` is present and at least 32 characters (256 bits) long.
   - **No Sensitive Data Leaks**: Passwords are never returned in responses, never logged, and never stored in plain text.
   - **Token Storage Isolation**: Frontend token management is isolated behind `TokenStorageService`, preparing for a seamless transition to HttpOnly cookies if required.

---

## 5. API Endpoints

All endpoints use the `/api/...` prefix:

| Method | Endpoint | Access | Description | Response Status |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register new aspirant account | `201 Created` / `400` / `409` |
| `POST` | `/api/auth/login` | Public | Authenticate user & issue JWT token | `200 OK` / `400` / `401` |
| `GET` | `/api/auth/me` | **Protected (Bearer)** | Get current authenticated user profile | `200 OK` / `401` |
| `GET` | `/api/health` | Public | Application uptime & version status | `200 OK` |
| `GET` | `/api/health/db` | Public | PostgreSQL connection verification | `200 OK` / `503` |

### Swagger / OpenAPI UI
When running the backend, interactive Swagger API documentation is available at:
* **`http://localhost:8080/swagger-ui.html`**
* OpenAPI Specification: `http://localhost:8080/v3/api-docs`

---

## 6. Environment Configuration

Copy `.env.example` to `.env` or set environment variables:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aspirantos
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

# JWT Authentication
JWT_SECRET=replace-with-a-very-strong-secret-key-at-least-32-chars-long!
JWT_EXPIRATION=86400000

# Application Port
PORT=8080
```

---

## 7. How to Run the Applications

### 7.1 Running the Backend (Spring Boot)

```bash
cd backend
# Windows
mvnw.cmd spring-boot:run
# macOS / Linux
./mvnw spring-boot:run
```

The Spring Boot backend will start on **`http://localhost:8080`**.

### 7.2 Running the Frontend (Angular)

```bash
cd frontend
# Install dependencies (first time only)
npm install

# Start the development server
npm start
```

The Angular frontend will start on **`http://localhost:4200`**.

---

## 8. Running Automated Tests

### Backend Unit & Integration Tests (21 Tests)
```bash
cd backend
mvnw.cmd test    # Windows
./mvnw test       # macOS / Linux
```
* **Coverage**: `AuthControllerTest`, `AuthServiceTest`, `HealthControllerTest`, `JwtServiceTest`, `SecurityFilterChainTest`.

### Frontend Build & Typecheck
```bash
cd frontend
npm run build
```

---

## 9. Development Status & Roadmap

* [x] **Step 1: Full-Stack Project Foundation** (Complete)
* [x] **Step 2: Authentication & User Management** (Complete)
  * [x] PostgreSQL `users` table & JPA User entity
  * [x] BCrypt password hashing & stateless JWT token lifecycle
  * [x] Spring Security filter chain with public/protected route authorization
  * [x] Angular Standalone Login, Register, & Verification Dashboard components
  * [x] `TokenStorageService`, `authGuard`, and `authInterceptor`
  * [x] Swagger OpenAPI documentation (`/swagger-ui.html`)
  * [x] Full test coverage (21 backend unit & integration tests, frontend spec tests)
* [ ] **Step 3: UPSC Syllabus & Subject Architecture** *(Next Step)*
* [ ] **Step 4: Study Tracker & Active Sessions**
* [ ] **Step 5: Revision System & Spaced Repetition**
* [ ] **Step 6: Previous Year Questions (PYQs) Engine**
* [ ] **Step 7: Command Center Analytics & Dashboards**
