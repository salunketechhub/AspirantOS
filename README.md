# AspirantOS — UPSC Preparation Command Center

<div align="center">

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Vercel-black?style=for-the-badge&logo=vercel)](https://aspirant-os-one.vercel.app/)
[![Backend API](https://img.shields.io/badge/Backend%20API-Railway-0B0D0E?style=for-the-badge&logo=railway)](https://aspirantos-backend-production.up.railway.app)
[![Swagger Docs](https://img.shields.io/badge/API%20Docs-Swagger%20UI-85EA2D?style=for-the-badge&logo=swagger)](https://aspirantos-backend-production.up.railway.app/swagger-ui/index.html)

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot 3.4](https://img.shields.io/badge/Spring%20Boot-3.4.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular 20](https://img.shields.io/badge/Angular-20-DD0031?style=flat-square&logo=angular&logoColor=white)](https://angular.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)

**"Plan. Study. Revise. Track."**  
*A modern, distraction-free command center for UPSC Civil Services aspirants to structure their preparation, track syllabus completion, and monitor past-year question mastery.*

[Live Web App](https://aspirant-os-one.vercel.app/) • [API Documentation](https://aspirantos-backend-production.up.railway.app/swagger-ui/index.html) • [Report Issue](https://github.com/salunketechhub/AspirantOS/issues)

</div>

---

## 🌟 Live Deployments

| Component | Platform | URL |
| :--- | :--- | :--- |
| **Frontend Web App** | **Vercel** | [https://aspirant-os-one.vercel.app/](https://aspirant-os-one.vercel.app/) |
| **Backend REST API** | **Railway** | [https://aspirantos-backend-production.up.railway.app](https://aspirantos-backend-production.up.railway.app) |
| **Interactive API Docs** | **Swagger UI** | [https://aspirantos-backend-production.up.railway.app/swagger-ui/index.html](https://aspirantos-backend-production.up.railway.app/swagger-ui/index.html) |
| **Database** | **Railway** | Managed PostgreSQL Cloud Cluster |

> **Demo Login Credentials:**  
> • **Email:** `harshitayadav@gmail.com`  
> • **Password:** `Password@123`  
> *(Or create a new account in 5 seconds via the Register page)*

---

## ✨ Key Features

### 1. 📚 Complete UPSC Civil Services Syllabus
Structured strictly according to official UPSC guidelines and top toppers' notes:
* **Prelims Examination**:
  * **General Studies Paper I (12 Topics)**: Ancient Indian History, Medieval Indian History, Modern Indian History, Art & Culture, Indian & World Geography, Indian Polity & Governance, Economic & Social Development, Environmental Ecology & Biodiversity, General Science, Current Events (National & International), Science & Technology Applications, Social Development & Demographics.
  * **CSAT Paper II (3 Topics)**: Comprehension & Interpersonal Skills, Logical Reasoning & Analytical Ability, Basic Numeracy & Data Interpretation.
* **Mains Examination (5 Papers / 21 Core Units)**:
  * **Essay (Paper I)**: Education, Women, Environment, Health & Science, Polity, Philosophical Themes.
  * **General Studies I (Paper II)**: Art & Culture, Modern History of India, World History, Indian Society, Geography.
  * **General Studies II (Paper III)**: Indian Polity, Social Justice, Governance, International Relations.
  * **General Studies III (Paper IV)**: Indian Economy, Science & Technology, Ecology & Environment, Disaster Management, Internal Security.
  * **General Studies IV (Paper V)**: Ethics, Integrity and Aptitude.
* **Optional Subjects**:
  * **Sociology (Drishti IAS Structured Syllabus)**:
    * *Paper I (10 Topics)*: Fundamentals of Sociology, Thinkers (Marx, Durkheim, Weber, Parsons, Merton, Mead), Stratification, Work & Economic Life, Politics, Religion, Family, Social Change.
    * *Paper II (15 Topics)*: Introducing Indian Society, Social Structure (Caste, Tribal, Agrarian, Classes), Social Transformations, Challenges in India.
  * **Optional Subjects Catalogue**: Searchable catalogue of 25+ UPSC optional subjects.

### 2. 📋 1-Click PYQ (Previous Year Questions) Tracker
* Dedicated **`○ PYQ Pending / ✓ PYQ Done`** toggle button directly on every syllabus topic card.
* Instantly tracks whether past year questions (Prelims MCQs / Mains answer writing) have been solved for that specific topic.
* Displays paper-level summary (e.g. `📋 PYQs: 8/12 Solved`).

### 3. 📊 Smart Progress Analytics & Dashboard
* **Dynamic Completion Calculations**: Instant recalculation of overall progress, Prelims %, Mains %, and Optional %.
* **PYQ Mastery Widget**: Real-time progress bar computing overall PYQ solved percentage.
* **Optimistic UI Updates**: State updates instantly with background database synchronization.

### 4. 🔒 Enterprise-Grade Security
* Stateless JWT (HMAC-SHA256) authentication with Bearer token validation.
* BCrypt password hashing (strength 12).
* Global Exception handling with structured, secure JSON error responses.
* Fine-grained CORS configuration allowing multi-cloud communication.

---

## 🛠️ Technology Stack

```
Frontend (Vercel)              Backend (Railway)             Database (Railway)
┌──────────────────────┐       ┌──────────────────────┐      ┌──────────────────────┐
│  Angular 20 (SPA)    │ ───►  │  Spring Boot 3.4.x   │ ───► │  PostgreSQL 16 DB    │
│  Tailwind CSS 3.4    │ JSON  │  Spring Security 6   │ JDBC │  Relational Schema   │
│  TypeScript / Signals│       │  JJWT / Hibernate    │ TLS  │  Indexes & Cascades  │
└──────────────────────┘       └──────────────────────┘      └──────────────────────┘
```

* **Frontend**: Angular 20, TypeScript 5.9, Tailwind CSS, Outfit & Plus Jakarta Sans fonts.
* **Backend**: Java 21 LTS, Spring Boot 3.4.3, Spring Security 6, Spring Data JPA, Hibernate 6.6.
* **Database**: PostgreSQL 16 with HikariCP connection pooling and automatic SSL negotiation.
* **Containerization & CI/CD**: Docker multi-stage builds, GitHub Actions, Railway & Vercel Webhooks.

---

## 📁 Repository Architecture

```
AspirantOS/
├── backend/                       # Spring Boot 3.4 REST API
│   ├── src/main/java/com/aspirantos/
│   │   ├── config/                # Security, CORS, DataSource & Data Seeder
│   │   ├── controller/            # Auth, Progress, Syllabus & Health Endpoints
│   │   ├── dto/                   # Request / Response DTO Records
│   │   ├── entity/                # JPA Entities (User, Exam, Subject, Topic, Progress)
│   │   ├── exception/             # Custom Exceptions & Global Exception Handler
│   │   ├── repository/            # Spring Data JPA Repositories
│   │   ├── security/              # JWT Filters, Token Provider & UserDetails
│   │   └── service/               # Business Logic Implementations
│   ├── src/test/                  # 50 Automated Unit & Integration Tests
│   ├── Dockerfile                 # Multi-stage JRE container
│   └── pom.xml                    # Maven Dependencies & Build Config
│
├── frontend/                      # Angular 20 Standalone Application
│   ├── src/app/
│   │   ├── core/                  # Auth Guards, Interceptors & Services
│   │   ├── models/                # TypeScript Interfaces & Types
│   │   ├── pages/                 # Home, Login, Register, Dashboard, Syllabus
│   │   └── services/              # Syllabus & Progress HTTP Services
│   ├── src/environments/          # Environment configuration (Dev & Prod)
│   ├── Dockerfile                 # Nginx-based production bundle
│   └── vercel.json                # Vercel SPA routing configuration
│
├── docker-compose.yml             # Local multi-container development orchestration
├── Dockerfile                     # Root multi-stage Docker build
└── README.md                      # Project documentation
```

---

## 🚀 Getting Started Locally

### Prerequisites
* **Java**: JDK 21+ installed
* **Node.js**: Node 20+ and npm
* **PostgreSQL**: PostgreSQL 15+ running locally (or Docker)

### Option 1: Run with Docker Compose (1 Command)

```bash
git clone https://github.com/salunketechhub/AspirantOS.git
cd AspirantOS
docker compose up -d --build
```
* **Frontend**: `http://localhost:4200`
* **Backend API**: `http://localhost:8080/api`
* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

---

### Option 2: Run Backend & Frontend Separately

#### 1. Backend Setup:
```bash
cd backend

# On Windows:
.\mvnw.cmd spring-boot:run

# On macOS/Linux:
./mvnw spring-boot:run
```
Backend will start on `http://localhost:8080`.

#### 2. Frontend Setup:
```bash
cd frontend

npm install
npm start
```
Frontend will start on `http://localhost:4200`.

---

## 📡 REST API Summary

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/auth/register` | Register a new aspirant account | No |
| `POST` | `/api/auth/login` | Authenticate user & receive JWT token | No |
| `GET` | `/api/health` | Service health status check | No |
| `GET` | `/api/health/db` | Database connectivity check | No |
| `GET` | `/api/syllabus/exams` | Retrieve all UPSC exam stages (Prelims, Mains, Optional) | Yes |
| `GET` | `/api/syllabus/exams/{id}/subjects` | Retrieve subjects / papers for an exam stage | Yes |
| `GET` | `/api/syllabus/subjects/{id}/topics` | Retrieve topics under a paper | Yes |
| `GET` | `/api/syllabus/optionals` | Retrieve Optional subjects catalogue | Yes |
| `GET` | `/api/progress` | Get overall, stage-wise, and PYQ completion metrics | Yes |
| `GET` | `/api/progress/all` | Bulk map of all topic completion statuses | Yes |
| `GET` | `/api/progress/pyq-map` | Bulk map of all topic PYQ statuses | Yes |
| `PUT` | `/api/progress/topics/{id}` | Update topic status (`NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`) | Yes |
| `POST`| `/api/progress/topics/{id}/pyq/toggle` | Toggle PYQ solved state for a topic | Yes |

---

## 🧪 Testing

The backend includes a comprehensive suite of **50 unit and integration tests** covering security filters, JWT generation, database operations, and controllers:

```bash
cd backend
.\mvnw.cmd test
```

Frontend production build verification:
```bash
cd frontend
npm run build
```

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
Built with ❤️ for UPSC Aspirants by <a href="https://github.com/salunketechhub"><strong>Salunke Tech Hub</strong></a>
</div>
