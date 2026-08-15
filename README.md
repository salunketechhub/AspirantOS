# AspirantOS

> **"Plan. Study. Revise. Track."**  
> *UPSC Preparation Command Center*

---

## 1. Project Overview

**AspirantOS** is a specialized, full-stack web application architected for Union Public Service Commission (UPSC) Civil Services examination preparation. It is designed to replace fragmented trackers, spreadsheets, and notebooks with an integrated preparation operating system.

> **Current Development Status**: **Step 1 — Project Foundation**  
> This milestone establishes the end-to-end full-stack foundation, including Angular standalone architecture, Spring Boot modular monolith, PostgreSQL database connectivity, CORS configuration, health diagnostics, and environment setups.

---

## 2. Technology Stack

### Frontend
* **Framework**: Angular 20+ (Standalone Components, modern signals, inject API)
* **Language**: TypeScript 5.9+
* **Routing**: Angular Router
* **Networking**: Angular `HttpClient` with `withFetch()`
* **Styling**: Tailwind CSS & PostCSS
* **Typography**: Outfit & Plus Jakarta Sans

### Backend
* **Runtime**: Java 21 LTS
* **Framework**: Spring Boot 3.4.x
* **Core Modules**: Spring Web, Spring Data JPA, Hibernate, Bean Validation
* **Build System**: Maven 3.9+ with Maven Wrapper (`mvnw`)
* **Utilities**: Lombok, Slf4j

### Database
* **Database Engine**: PostgreSQL 15+ (Default port: `5432`)
* **Default Database Name**: `aspirantos`

---

## 3. Project Structure

```
AspirantOS/
├── .env.example              # Environment variables template
├── .gitignore                # Git ignore rules for root, frontend, and backend
├── README.md                 # Project documentation
│
├── database/                 # Database initialization and documentation
│   ├── init.sql              # Schema baseline & extensions script
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
│       │   │   │   └── CorsConfig.java            # Development & production CORS mapping
│       │   │   ├── controller/
│       │   │   │   └── HealthController.java      # /api/health and /api/health/db REST endpoints
│       │   │   ├── dto/
│       │   │   │   ├── HealthResponse.java        # Application health record
│       │   │   │   └── DatabaseHealthResponse.java# Database health record
│       │   │   ├── service/
│       │   │   │   ├── HealthService.java         # Health service interface
│       │   │   │   └── HealthServiceImpl.java     # Connectivity implementation
│       │   │   └── exception/
│       │   │       └── GlobalExceptionHandler.java# Centralized exception handler
│       │   └── resources/
│       │       └── application.yml                # Spring Boot configuration
│       └── test/
│           └── java/com/aspirantos/
│               └── controller/
│                   └── HealthControllerTest.java  # MockMvc unit tests
│
└── frontend/                 # Angular 20+ frontend application
    ├── package.json          # Node dependencies & npm scripts
    ├── angular.json          # Angular CLI workspace config
    ├── tsconfig.json         # TypeScript compiler config
    ├── tailwind.config.js    # Tailwind CSS design system tokens
    ├── postcss.config.js     # PostCSS configuration
    └── src/
        ├── index.html        # HTML entry point with modern typography
        ├── main.ts           # Standalone application bootstrap
        ├── styles.css        # Tailwind base styles and theme rules
        ├── environments/     # Environment-specific configuration
        │   ├── environment.ts            # Production environment
        │   └── environment.development.ts# Local development (http://localhost:8080/api)
        └── app/
            ├── app.config.ts             # Application providers (Router, HttpClient)
            ├── app.routes.ts             # Route definitions
            ├── app.ts                    # Root component
            ├── models/
            │   └── health.model.ts       # TypeScript interfaces for API responses
            ├── services/
            │   └── api.service.ts        # Backend HTTP client service
            └── pages/
                └── home/
                    ├── home.component.ts # Reactive health status UI logic
                    ├── home.component.html
                    └── home.component.css
```

---

## 4. Prerequisites

Before running the application, make sure the following tools are installed:

1. **Java Development Kit (JDK)**: JDK 21 LTS or newer.
2. **Node.js**: Node.js v20+ / v22+ / v24+ with npm.
3. **PostgreSQL**: PostgreSQL 15+ running locally or in Docker.

---

## 5. PostgreSQL Setup

> **Important**: PostgreSQL must be running and the target database (`aspirantos`) created before the backend can successfully verify a database connection.

### Step 5.1: Start PostgreSQL Service
Ensure your PostgreSQL server is active:
* **Windows**: `net start postgresql-x64-16` or via Windows Services (`services.msc`).
* **macOS**: `brew services start postgresql@16`
* **Linux**: `sudo systemctl start postgresql`
* **Docker**:
  ```bash
  docker run --name aspirantos-postgres \
    -e POSTGRES_DB=aspirantos \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -p 5432:5432 -d postgres:16-alpine
  ```

### Step 5.2: Create the `aspirantos` Database
Run via `psql`:
```bash
psql -U postgres -c "CREATE DATABASE aspirantos;"
```

### Step 5.3: Run the Schema Baseline (Optional / Recommended)
```bash
psql -U postgres -d aspirantos -f database/init.sql
```

---

## 6. Environment Configuration

Copy `.env.example` to `.env` or set system environment variables:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aspirantos
DB_USERNAME=postgres
DB_PASSWORD=your_password_here

# Server Configuration
PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Frontend Configuration
FRONTEND_PORT=4200
```

> **Security Note**: Never commit `.env` files containing production passwords or secrets into source control.

---

## 7. How to Run the Application

Both backend and frontend applications can be run independently in separate terminal windows.

### 7.1 Running the Backend (Spring Boot)

Open a terminal in the `backend/` directory:

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

The Spring Boot backend will start on **`http://localhost:8080`**.

### 7.2 Running the Frontend (Angular)

Open a terminal in the `frontend/` directory:

```bash
# Install dependencies (first time only)
npm install

# Start the Angular development server
npm start
```

The Angular frontend will start on **`http://localhost:4200`**.

---

## 8. API Endpoints

All backend APIs are prefixed with `/api/...`:

| Method | Endpoint | Description | Sample Response |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/health` | Application health check | `{"status": "UP", "application": "AspirantOS", "message": "Backend is running successfully"}` |
| `GET` | `/api/health/db` | Database connectivity check | `{"status": "UP", "database": "PostgreSQL", "message": "Database connection successful"}` |

---

## 9. Running Tests

### Backend Unit Tests
```bash
cd backend
mvnw.cmd test    # Windows
./mvnw test       # macOS / Linux
```

### Frontend Production Build Verification
```bash
cd frontend
npm run build
```

---

## 10. Development Status

* [x] **Step 1: Project Foundation** (Current Milestone)
  * [x] Monolith project structure (Angular + Spring Boot + PostgreSQL)
  * [x] Standalone Angular 20+ app with Tailwind CSS
  * [x] Spring Boot 3.4+ REST architecture & CORS support
  * [x] PostgreSQL connection verification & health check endpoints
  * [x] Environment configuration & setup documentation
* [ ] **Step 2: Authentication & Core User Management** *(Future Step)*
* [ ] **Step 3: UPSC Syllabus & Subject Architecture** *(Future Step)*
* [ ] **Step 4: Study Tracker & Active Sessions** *(Future Step)*
* [ ] **Step 5: Revision System & Spaced Repetition** *(Future Step)*
* [ ] **Step 6: Previous Year Questions (PYQs) Engine** *(Future Step)*
* [ ] **Step 7: Analytics & Command Center Dashboard** *(Future Step)*
