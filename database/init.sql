-- ==============================================================================
-- AspirantOS - Database Initialization Script
-- ==============================================================================
-- Database: aspirantos
-- Purpose: Schema setup, baseline verification, and Step 2 User Management table
-- ==============================================================================

-- 1. Verify extension availability (UUID generator)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Schema baseline metadata (reserved for versioning tracking)
CREATE TABLE IF NOT EXISTS _schema_version (
    id SERIAL PRIMARY KEY,
    version VARCHAR(32) NOT NULL,
    description VARCHAR(255) NOT NULL,
    installed_on TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Users Table (Step 2: Authentication & User Management)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users (LOWER(email));

-- 4. Record schema versioning
INSERT INTO _schema_version (version, description)
SELECT '1.1.0', 'Step 2: Authentication & User Management Schema'
WHERE NOT EXISTS (
    SELECT 1 FROM _schema_version WHERE version = '1.1.0'
);
