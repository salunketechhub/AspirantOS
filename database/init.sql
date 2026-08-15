-- ==============================================================================
-- AspirantOS - Database Initialization Script
-- ==============================================================================
-- Database: aspirantos
-- Purpose: Schema setup, baseline verification, User Management, and Step 3 Syllabus Architecture
-- ==============================================================================

-- 1. Verify extension availability (UUID generator)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Schema baseline metadata
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

-- ==============================================================================
-- Step 3: UPSC Syllabus & Subject Architecture Tables
-- ==============================================================================

-- 4. Exams Table (Prelims, Mains)
CREATE TABLE IF NOT EXISTS exams (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    stage VARCHAR(30) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_exams_stage ON exams (stage);

-- 5. Subjects Table (Papers within an Exam stage)
CREATE TABLE IF NOT EXISTS subjects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    exam_id UUID NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    paper VARCHAR(50) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_subjects_exam ON subjects (exam_id);

-- 6. Syllabus Topics Table (Recursive topic hierarchy)
CREATE TABLE IF NOT EXISTS syllabus_topics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subject_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    parent_topic_id UUID REFERENCES syllabus_topics(id) ON DELETE CASCADE,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    level INT NOT NULL DEFAULT 1,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_topics_subject ON syllabus_topics (subject_id);
CREATE INDEX IF NOT EXISTS idx_topics_parent ON syllabus_topics (parent_topic_id);
CREATE INDEX IF NOT EXISTS idx_topics_code ON syllabus_topics (code);

-- 7. Optional Subjects Catalogue Table
CREATE TABLE IF NOT EXISTS optional_subjects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- Deterministic Seed Data (Step 3)
-- ==============================================================================

-- Seed Exams
INSERT INTO exams (id, code, name, description, stage, display_order)
VALUES 
    ('a0000000-0000-0000-0000-000000000001', 'PRELIMS', 'UPSC Civil Services (Preliminary) Examination', 'Objective screening test consisting of General Studies Paper I and CSAT Paper II.', 'PRELIMS', 1),
    ('a0000000-0000-0000-0000-000000000002', 'MAINS', 'UPSC Civil Services (Main) Examination', 'Written descriptive test consisting of Essay, 4 General Studies papers, and Optional papers.', 'MAINS', 2)
ON CONFLICT (code) DO NOTHING;

-- Seed Subjects
INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order)
VALUES
    -- Prelims Subjects
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'PRELIMS_GS1', 'General Studies Paper I', 'Current events, History of India, Geography, Polity, Governance, Economic Development, Environmental Ecology, and General Science.', 'Paper I', 1),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'PRELIMS_CSAT', 'CSAT / Paper II', 'Comprehension, interpersonal skills, logical reasoning, analytical ability, decision-making, and basic numeracy.', 'Paper II', 2),
    
    -- Mains Subjects
    ('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002', 'MAINS_ESSAY', 'Essay', 'Candidates are required to write essays on multiple philosophical, socio-economic, and administrative themes.', 'Essay', 1),
    ('b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000002', 'MAINS_GS1', 'General Studies I', 'Indian Heritage and Culture, History and Geography of the World and Society.', 'GS Paper I', 2),
    ('b0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000002', 'MAINS_GS2', 'General Studies II', 'Governance, Constitution, Polity, Social Justice and International Relations.', 'GS Paper II', 3),
    ('b0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000002', 'MAINS_GS3', 'General Studies III', 'Technology, Economic Development, Biodiversity, Environment, Security and Disaster Management.', 'GS Paper III', 4),
    ('b0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000002', 'MAINS_GS4', 'General Studies IV', 'Ethics, Integrity, and Aptitude.', 'GS Paper IV', 5)
ON CONFLICT (code) DO NOTHING;

-- Seed Syllabus Topics & Subtopics
INSERT INTO syllabus_topics (id, subject_id, parent_topic_id, code, name, description, level, display_order)
VALUES
    -- Prelims GS1 Topics & Subtopics
    ('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', NULL, 'PGS1_POLITY', 'Indian Polity & Governance', 'Constitution, Political System, Panchayati Raj, Public Policy, Rights Issues, etc.', 1, 1),
    ('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'PGS1_POLITY_CONST', 'Constitutional Framework & Organs of State', 'Preamble, Fundamental Rights, DPSP, Parliament, Executive, and Judiciary.', 2, 1),
    ('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'PGS1_POLITY_LOCAL', 'Panchayati Raj & Local Governance', '73rd and 74th Constitutional Amendment Acts, Urban Local Bodies, Decentralization.', 2, 2),

    ('c0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000001', NULL, 'PGS1_HIST', 'History of India & Indian National Movement', 'Ancient, Medieval, and Modern Indian history with focus on freedom struggle.', 1, 2),
    ('c0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000004', 'PGS1_HIST_ANC_MED', 'Ancient & Medieval India', 'Indus Valley, Vedic Age, Mauryan, Gupta, Delhi Sultanate, and Mughal Empire.', 2, 1),
    ('c0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000004', 'PGS1_HIST_MODERN', 'Modern Indian History & National Movement', 'British expansion, 1857 Revolt, INC, Gandhian Era, and partition/independence.', 2, 2),

    ('c0000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000001', NULL, 'PGS1_GEO', 'Indian and World Geography', 'Physical, Social, and Economic Geography of India and the World.', 1, 3),
    ('c0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000007', 'PGS1_GEO_PHYSICAL', 'Physical Geography & Geomorphology', 'Earth structure, plate tectonics, climatology, oceanography, Indian physiography.', 2, 1),
    ('c0000000-0000-0000-0000-000000000009', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000007', 'PGS1_GEO_RESOURCES', 'Economic Geography & Resources', 'Agriculture, minerals, industries, infrastructure, energy resources.', 2, 2),

    ('c0000000-0000-0000-0000-000000000010', 'b0000000-0000-0000-0000-000000000001', NULL, 'PGS1_ECON', 'Economic and Social Development', 'Sustainable Development, Poverty, Inclusion, Demographics, Social Sector Initiatives.', 1, 4),
    ('c0000000-0000-0000-0000-000000000011', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000010', 'PGS1_ECON_MACRO', 'Macroeconomics, Fiscal & Monetary Policy', 'National income accounting, inflation, RBI policies, banking, taxation, and budget.', 2, 1),

    ('c0000000-0000-0000-0000-000000000012', 'b0000000-0000-0000-0000-000000000001', NULL, 'PGS1_ENV', 'Environmental Ecology, Biodiversity & Climate Change', 'General issues that do not require subject specialization.', 1, 5),
    ('c0000000-0000-0000-0000-000000000013', 'b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000012', 'PGS1_ENV_BIODIV', 'Biodiversity & Conservation Efforts', 'Protected area networks, IUCN status, wildlife acts, Ramsar wetlands.', 2, 1),

    -- Prelims CSAT Topics
    ('c0000000-0000-0000-0000-000000000014', 'b0000000-0000-0000-0000-000000000002', NULL, 'CSAT_RC', 'Reading Comprehension', 'Short and long passages testing critical comprehension and inference skills.', 1, 1),
    ('c0000000-0000-0000-0000-000000000015', 'b0000000-0000-0000-0000-000000000002', NULL, 'CSAT_LR', 'Logical Reasoning & Analytical Ability', 'Syllogisms, seating arrangement, coding-decoding, blood relations, and puzzles.', 1, 2),
    ('c0000000-0000-0000-0000-000000000016', 'b0000000-0000-0000-0000-000000000002', NULL, 'CSAT_QA', 'Quantitative Aptitude & Basic Numeracy', 'Numbers, percentage, profit & loss, ratio, permutations, and data interpretation.', 1, 3),

    -- Mains GS1 Topics
    ('c0000000-0000-0000-0000-000000000017', 'b0000000-0000-0000-0000-000000000004', NULL, 'MGS1_ART', 'Indian Art, Architecture & Culture', 'Salient aspects of Art Forms, Literature and Architecture from ancient to modern times.', 1, 1),
    ('c0000000-0000-0000-0000-000000000018', 'b0000000-0000-0000-0000-000000000004', NULL, 'MGS1_MOD_HIST', 'Modern Indian History & Freedom Struggle', 'Significant events, personalities, and stages of the freedom movement.', 1, 2),
    ('c0000000-0000-0000-0000-000000000019', 'b0000000-0000-0000-0000-000000000004', NULL, 'MGS1_WORLD_HIST', 'History of the World', 'Events from 18th century such as Industrial Revolution, World Wars, Decolonization, and Political Philosophies.', 1, 3),
    ('c0000000-0000-0000-0000-000000000020', 'b0000000-0000-0000-0000-000000000004', NULL, 'MGS1_SOCIETY', 'Salient Features of Indian Society & Diversity', 'Role of women, population issues, poverty, urbanization, effects of globalization, communalism, regionalism, secularism.', 1, 4),
    ('c0000000-0000-0000-0000-000000000021', 'b0000000-0000-0000-0000-000000000004', NULL, 'MGS1_GEO', 'World & Indian Physical Geography', 'Distribution of key natural resources, factors responsible for industrial locations, geophysical phenomena.', 1, 5),

    -- Mains GS2 Topics
    ('c0000000-0000-0000-0000-000000000022', 'b0000000-0000-0000-0000-000000000005', NULL, 'MGS2_CONST', 'Indian Constitution & Federalism', 'Evolution, amendments, significant provisions, basic structure, federal issues, separation of powers.', 1, 1),
    ('c0000000-0000-0000-0000-000000000023', 'b0000000-0000-0000-0000-000000000005', NULL, 'MGS2_GOV', 'Governance, Transparency & Accountability', 'E-governance, citizen charters, role of civil services, statutory, regulatory and quasi-judicial bodies.', 1, 2),
    ('c0000000-0000-0000-0000-0000000000024', 'b0000000-0000-0000-0000-000000000005', NULL, 'MGS2_SOC_JUST', 'Social Justice & Welfare Initiatives', 'Welfare schemes for vulnerable sections, health, education, human resources, issues relating to poverty and hunger.', 1, 3),
    ('c0000000-0000-0000-0000-000000000025', 'b0000000-0000-0000-0000-000000000005', NULL, 'MGS2_IR', 'International Relations & Global Groupings', 'India and its neighborhood, bilateral, regional and global groupings, effect of policies of developed/developing nations.', 1, 4),

    -- Mains GS3 Topics
    ('c0000000-0000-0000-0000-000000000026', 'b0000000-0000-0000-0000-000000000006', NULL, 'MGS3_ECON', 'Indian Economy & Inclusive Growth', 'Planning, mobilization of resources, growth, development, employment, and government budgeting.', 1, 1),
    ('c0000000-0000-0000-0000-000000000027', 'b0000000-0000-0000-0000-000000000006', NULL, 'MGS3_AGRI', 'Agriculture & Food Processing', 'Major crops, cropping patterns, irrigation, direct & indirect farm subsidies, MSP, PDS, economics of animal-rearing.', 1, 2),
    ('c0000000-0000-0000-0000-000000000028', 'b0000000-0000-0000-0000-000000000006', NULL, 'MGS3_SCI_TECH', 'Science & Technology Developments', 'Indigenization of technology, IT, space, computers, robotics, nanotechnology, biotechnology, and IPR.', 1, 3),
    ('c0000000-0000-0000-0000-000000000029', 'b0000000-0000-0000-0000-000000000006', NULL, 'MGS3_ENV_DM', 'Environment Conservation & Disaster Management', 'Pollution, degradation, environmental impact assessment, disaster and disaster management mechanisms.', 1, 4),
    ('c0000000-0000-0000-0000-000000000030', 'b0000000-0000-0000-0000-000000000006', NULL, 'MGS3_SEC', 'Internal Security & Border Management', 'Linkages between development and spread of extremism, cyber security, money laundering, security forces.', 1, 5),

    -- Mains GS4 Topics
    ('c0000000-0000-0000-0000-000000000031', 'b0000000-0000-0000-0000-000000000007', NULL, 'MGS4_ETHICS', 'Ethics and Human Interface', 'Essence, determinants and consequences of Ethics in human actions; dimensions of ethics; ethics in private and public relationships.', 1, 1),
    ('c0000000-0000-0000-0000-000000000032', 'b0000000-0000-0000-0000-000000000007', NULL, 'MGS4_HUMAN_VAL', 'Human Values & Thinkers', 'Lessons from the lives and teachings of great leaders, reformers and administrators; role of family, society and educational institutions.', 1, 2),
    ('c0000000-0000-0000-0000-000000000033', 'b0000000-0000-0000-0000-000000000007', NULL, 'MGS4_ATTITUDE', 'Attitude & Emotional Intelligence', 'Content, structure, function; its influence and relation with thought and behaviour; moral and political attitudes; social influence and persuasion.', 1, 3),
    ('c0000000-0000-0000-0000-000000000034', 'b0000000-0000-0000-0000-000000000007', NULL, 'MGS4_PROBITY', 'Probity in Governance & Public Service Values', 'Concept of public service; philosophical basis of governance and probity; information sharing, transparency, RTI, codes of ethics.', 1, 4),
    ('c0000000-0000-0000-0000-000000000035', 'b0000000-0000-0000-0000-000000000007', NULL, 'MGS4_CASES', 'Case Studies on Ethical Dilemmas', 'Practical scenario case studies on issues involving corruption, administrative ethics, and public welfare.', 1, 5)
ON CONFLICT (id) DO NOTHING;

-- Seed Optional Subjects Catalogue
INSERT INTO optional_subjects (id, code, name, description, display_order)
VALUES
    ('d0000000-0000-0000-0000-000000000001', 'OPT_PUB_AD', 'Public Administration', 'Administrative theory, Indian administration, public policy, financial administration, and rural-urban development.', 1),
    ('d0000000-0000-0000-0000-000000000002', 'OPT_PSIR', 'Political Science & International Relations', 'Political theory, Indian nationalism, comparative politics, and India’s foreign policy.', 2),
    ('d0000000-0000-0000-0000-000000000003', 'OPT_SOCIO', 'Sociology', 'Sociological thinkers, social stratification, politics and society, religion, family, and social change in India.', 3),
    ('d0000000-0000-0000-0000-000000000004', 'OPT_GEO', 'Geography', 'Geomorphology, climatology, oceanography, population geography, regional planning, and geography of India.', 4),
    ('d0000000-0000-0000-0000-000000000005', 'OPT_HIST', 'History', 'Sources, early Indian society, medieval political formations, colonial rule, and world history.', 5),
    ('d0000000-0000-0000-0000-000000000006', 'OPT_ANTHRO', 'Anthropology', 'Physical anthropology, sociocultural anthropology, Indian anthropology, and tribal communities.', 6),
    ('d0000000-0000-0000-0000-000000000007', 'OPT_ECON', 'Economics', 'Advanced micro & macroeconomics, public finance, international economics, growth & development, and Indian economy.', 7),
    ('d0000000-0000-0000-0000-000000000008', 'OPT_PHIL', 'Philosophy', 'Western philosophy, Indian philosophy, socio-political philosophy, and philosophy of religion.', 8)
ON CONFLICT (code) DO NOTHING;

-- 8. Record schema versioning for Step 3
INSERT INTO _schema_version (version, description)
SELECT '1.2.0', 'Step 3: UPSC Syllabus & Subject Architecture Schema & Seed Data'
WHERE NOT EXISTS (
    SELECT 1 FROM _schema_version WHERE version = '1.2.0'
);

-- ==============================================================================
-- Step 4: UPSC Syllabus Completion Tracking Tables
-- ==============================================================================

-- 9. User Topic Progress Table
CREATE TABLE IF NOT EXISTS user_topic_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    topic_id UUID NOT NULL REFERENCES syllabus_topics(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_topic UNIQUE (user_id, topic_id)
);

CREATE INDEX IF NOT EXISTS idx_progress_user ON user_topic_progress (user_id);
CREATE INDEX IF NOT EXISTS idx_progress_topic ON user_topic_progress (topic_id);
CREATE INDEX IF NOT EXISTS idx_progress_user_topic ON user_topic_progress (user_id, topic_id);
CREATE INDEX IF NOT EXISTS idx_progress_status ON user_topic_progress (status);

-- 10. Record schema versioning for Step 4
INSERT INTO _schema_version (version, description)
SELECT '1.3.0', 'Step 4: UPSC Syllabus Completion Tracking Schema'
WHERE NOT EXISTS (
    SELECT 1 FROM _schema_version WHERE version = '1.3.0'
);

