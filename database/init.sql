-- ==============================================================================
-- AspirantOS - Database Initialization Script
-- ==============================================================================
-- Database: aspirantos
-- Purpose: Schema setup and baseline verification for AspirantOS Step 1 Foundation
-- ==============================================================================

-- 1. Create database (Run manually if connected as superuser / postgres admin):
-- CREATE DATABASE aspirantos WITH OWNER postgres ENCODING 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';

-- 2. Verify connection & extension availability (connect to aspirantos database first)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 3. Schema baseline metadata (reserved for versioning tracking)
CREATE TABLE IF NOT EXISTS _schema_version (
    id SERIAL PRIMARY KEY,
    version VARCHAR(32) NOT NULL,
    description VARCHAR(255) NOT NULL,
    installed_on TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO _schema_version (version, description)
SELECT '1.0.0', 'Step 1: Project Foundation and Connectivity Verification'
WHERE NOT EXISTS (
    SELECT 1 FROM _schema_version WHERE version = '1.0.0'
);
