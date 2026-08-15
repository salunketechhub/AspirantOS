# AspirantOS — Database Setup Guide

This guide details the steps required to set up and initialize the PostgreSQL database for **AspirantOS**.

> **Important**: PostgreSQL must be running and the `aspirantos` database must exist before the Spring Boot backend can successfully establish a database connection.

---

## 1. Requirements

* **Database Engine**: PostgreSQL 15+ (PostgreSQL 16 or 17 recommended)
* **Default Port**: `5432`
* **Target Database**: `aspirantos`

---

## 2. Setting Up PostgreSQL

### Option A: Local Native PostgreSQL Installation (Recommended)

1. **Start PostgreSQL Service**:
   - **Windows**: Open Services (`services.msc`) -> start `postgresql-x64-XX` or run:
     ```powershell
     net start postgresql-x64-16
     ```
   - **macOS / Linux**:
     ```bash
     # macOS (Homebrew)
     brew services start postgresql@16

     # Linux (Systemd)
     sudo systemctl start postgresql
     ```

2. **Create the `aspirantos` Database**:
   Open a terminal and run `psql` as the `postgres` user:
   ```bash
   psql -U postgres
   ```
   Execute the following SQL commands:
   ```sql
   CREATE DATABASE aspirantos;
   \c aspirantos
   \i database/init.sql
   ```

---

### Option B: Docker Container (Alternative)

If you prefer running PostgreSQL in a container:

```bash
docker run --name aspirantos-postgres \
  -e POSTGRES_DB=aspirantos \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16-alpine
```

Execute initialization schema:
```bash
docker exec -i aspirantos-postgres psql -U postgres -d aspirantos < database/init.sql
```

---

## 3. Environment Variables

Configure the following environment variables (or rely on the defaults):

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `DB_HOST` | Host where PostgreSQL is running | `localhost` |
| `DB_PORT` | PostgreSQL listening port | `5432` |
| `DB_NAME` | Database name | `aspirantos` |
| `DB_USERNAME` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |

---

## 4. Verification

Verify that the database responds to queries:
```bash
psql -h localhost -p 5432 -U postgres -d aspirantos -c "SELECT version();"
```

Once running, start the Spring Boot backend and invoke `GET http://localhost:8080/api/health/db`.
