# AspirantOS — Step 3: UPSC Syllabus & Subject Architecture

## Overview & Goal
In Step 3, we built the complete database-driven **UPSC Syllabus & Subject Architecture** for AspirantOS, establishing an extensible academic hierarchy:
- **Exams / Stages**: `Prelims`, `Mains` (and extensible to `Interview`).
- **Subjects / Papers**: Prelims General Studies Paper I, CSAT Paper II, Mains Essay, General Studies Papers I through IV.
- **Hierarchical Topics & Subtopics**: Arbitrary recursive parent-child topic hierarchy (Level 1 topics with nested Level 2 subtopics).
- **Optional Subjects Catalogue**: Full reference catalogue of UPSC Main examination optional subjects.

---

## 1. Architecture & Design Patterns

### Database Schema (Version `1.2.0`)
```
exams (UUID id, code, name, description, stage, display_order)
  └── subjects (UUID id, exam_id, code, name, description, paper, display_order)
        └── syllabus_topics (UUID id, subject_id, parent_topic_id, code, name, description, level, display_order)

optional_subjects (UUID id, code, name, description, display_order)
```

- **UUID Primary Keys**: Everywhere for consistency and security.
- **Recursive Hierarchy**: `syllabus_topics.parent_topic_id` foreign key references `syllabus_topics.id` (`ON DELETE CASCADE`).
- **Performance**: In-memory single-query tree builder in `SyllabusServiceImpl` eliminates N+1 database roundtrips.
- **Stateless JWT Security**: All `/api/syllabus/**` endpoints require Bearer JWT authentication.

---

## 2. API Endpoints Implemented

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/syllabus` | Aggregated full syllabus hierarchy tree with summary statistics | `Yes` (JWT) |
| `GET` | `/api/syllabus/exams` | All exam stages (`PRELIMS`, `MAINS`) ordered by display order | `Yes` (JWT) |
| `GET` | `/api/syllabus/exams/{id}` | Specific exam stage details and paper counts | `Yes` (JWT) |
| `GET` | `/api/syllabus/exams/{id}/subjects` | All subjects/papers under a specific exam stage | `Yes` (JWT) |
| `GET` | `/api/syllabus/subjects/{id}` | Specific subject details | `Yes` (JWT) |
| `GET` | `/api/syllabus/subjects/{id}/topics` | Hierarchical recursive topic tree for a subject | `Yes` (JWT) |
| `GET` | `/api/syllabus/topics/{id}` | Specific topic node with nested child subtopics | `Yes` (JWT) |
| `GET` | `/api/syllabus/optionals` | Complete UPSC optional subjects catalogue | `Yes` (JWT) |

---

## 3. Frontend Implementation

- **Data-Driven Architecture**: The Angular UI is 100% data-driven via `SyllabusService`.
- **Stage Tabs**: Quick switching between Prelims, Mains, and Optionals.
- **Interactive Subject Pills**: Seamless selection of papers with topic count telemetry.
- **Recursive Topic Accordions**: Level 1 topic cards with expandable Level 2 subtopic trees.
- **Instant Search & Filter**: Real-time matching across topic names, codes, and descriptions with automatic ancestor node expansion.
- **Batch Actions**: "Expand All" and "Collapse All" controls.
- **Protected Routing**: Accessible via `/syllabus` with Angular `authGuard`.

---

## 4. Verification & Testing

- **Backend Tests**: 34 unit and integration tests executed with **0 failures and 0 errors**.
  - `SyllabusServiceTest` (5 tests)
  - `SyllabusControllerTest` (6 tests)
  - `SyllabusSecurityTest` (2 tests)
  - `AuthControllerTest` (6 tests)
  - `HealthControllerTest` (3 tests)
  - `JwtServiceTest` (4 tests)
  - `SecurityFilterChainTest` (4 tests)
  - `AuthServiceTest` (4 tests)
- **Frontend Production Build**: `npm run build` completed cleanly in 8.01 seconds with zero errors or bundle warnings.
- **Automatic Resilient Seeding**: Added `SyllabusDataSeeder` (`CommandLineRunner`) for plug-and-play seeding alongside `database/init.sql`.
