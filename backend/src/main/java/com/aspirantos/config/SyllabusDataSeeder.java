package com.aspirantos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class SyllabusDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SyllabusDataSeeder.class);

    private final JdbcTemplate jdbcTemplate;

    public SyllabusDataSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Ensure user_topic_progress table exists
        try {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS user_topic_progress (
                    id UUID PRIMARY KEY,
                    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                    topic_id UUID NOT NULL REFERENCES syllabus_topics(id) ON DELETE CASCADE,
                    status VARCHAR(30) NOT NULL,
                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT uq_user_topic UNIQUE (user_id, topic_id)
                )
            """);
        } catch (Exception e) {
            log.debug("Table user_topic_progress check: {}", e.getMessage());
        }

        log.info("Updating UPSC Syllabus with exact topic architecture from notes and Drishti IAS Sociology Optional...");
        Timestamp now = Timestamp.from(Instant.now());

        // Refresh syllabus topics hierarchy and update constraints if needed
        try {
            jdbcTemplate.execute("ALTER TABLE exams DROP CONSTRAINT IF EXISTS exams_stage_check");
            jdbcTemplate.execute("ALTER TABLE exams DROP CONSTRAINT IF EXISTS check_exam_stage");
            jdbcTemplate.execute("ALTER TABLE exams ADD CONSTRAINT exams_stage_check CHECK (stage IN ('PRELIMS', 'MAINS', 'OPTIONAL', 'INTERVIEW'))");
        } catch (Exception e) {
            log.debug("Exams stage constraint update: {}", e.getMessage());
        }

        jdbcTemplate.execute("DELETE FROM user_topic_progress");
        jdbcTemplate.execute("DELETE FROM syllabus_topics");
        jdbcTemplate.execute("DELETE FROM subjects");
        jdbcTemplate.execute("DELETE FROM exams");

        // 1. Exams (Prelims, Mains, Optional)
        UUID prelimsId = UUID.fromString("a0000000-0000-0000-0000-000000000001");
        UUID mainsId = UUID.fromString("a0000000-0000-0000-0000-000000000002");
        UUID optionalId = UUID.fromString("a0000000-0000-0000-0000-000000000003");

        jdbcTemplate.update(
                "INSERT INTO exams (id, code, name, description, stage, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                prelimsId, "PRELIMS", "UPSC Civil Services (Preliminary) Examination",
                "Objective screening test consisting of General Studies Paper I and CSAT Paper II.",
                "PRELIMS", 1, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO exams (id, code, name, description, stage, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                mainsId, "MAINS", "UPSC Civil Services (Main) Examination",
                "Written descriptive examination: Essay, General Studies Papers I to IV.",
                "MAINS", 2, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO exams (id, code, name, description, stage, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                optionalId, "OPTIONAL", "Optional Subjects",
                "Specialized Optional Subject papers (Paper I & Paper II, 500 marks).",
                "OPTIONAL", 3, now, now
        );

        // 2. Subjects
        UUID prelimsGs1Id = UUID.fromString("b0000000-0000-0000-0000-000000000001");
        UUID csatId = UUID.fromString("b0000000-0000-0000-0000-000000000002");
        UUID essayId = UUID.fromString("b0000000-0000-0000-0000-000000000003");
        UUID gs1Id = UUID.fromString("b0000000-0000-0000-0000-000000000004");
        UUID gs2Id = UUID.fromString("b0000000-0000-0000-0000-000000000005");
        UUID gs3Id = UUID.fromString("b0000000-0000-0000-0000-000000000006");
        UUID gs4Id = UUID.fromString("b0000000-0000-0000-0000-000000000007");
        UUID socioP1Id = UUID.fromString("b0000000-0000-0000-0000-000000000008");
        UUID socioP2Id = UUID.fromString("b0000000-0000-0000-0000-000000000009");

        insertSubject(prelimsGs1Id, prelimsId, "PRELIMS_GS1", "General Studies Paper I", "Current events, History (Ancient, Medieval, Modern), Art & Culture, Geography, Polity, Economy, Environment, and General Science.", "Paper I", 1, now);
        insertSubject(csatId, prelimsId, "PRELIMS_CSAT", "CSAT / Paper II", "Comprehension, Interpersonal skills, Logical reasoning, Analytical ability, Decision-making, General mental ability, and Basic numeracy.", "Paper II", 2, now);

        insertSubject(essayId, mainsId, "MAINS_ESSAY", "Essay", "Theme-based descriptive essays across Education, Women, Environment, Health & Science, Polity, and Philosophical dimensions.", "Paper I", 1, now);
        insertSubject(gs1Id, mainsId, "MAINS_GS1", "General Studies I", "Art & Culture, Modern History of India, Modern History of World, Indian Society, and Geography (12 Units).", "Paper II", 2, now);
        insertSubject(gs2Id, mainsId, "MAINS_GS2", "General Studies II", "Indian Polity, Social Justice, Governance, and International Relations (20 Units).", "Paper III", 3, now);
        insertSubject(gs3Id, mainsId, "MAINS_GS3", "General Studies III", "Indian Economy, Science and Technology, Ecology and Environment, Disaster Management, and Internal Security (20 Units).", "Paper IV", 4, now);
        insertSubject(gs4Id, mainsId, "MAINS_GS4", "General Studies IV", "Ethics, Integrity and Aptitude - Public service values, moral thinkers, probity in governance, and case studies.", "Paper V", 5, now);

        insertSubject(socioP1Id, optionalId, "OPT_SOCIO_P1", "Sociology Paper I", "Fundamentals of Sociology (10 Topics): Discipline, Science, Research Methods, Thinkers, Stratification, Works & Economic Life, Politics, Religion, Kinship, and Social Change.", "Paper I", 1, now);
        insertSubject(socioP2Id, optionalId, "OPT_SOCIO_P2", "Sociology Paper II", "Indian Society: Structure and Change (15 Topics across Parts A, B, C): Perspectives, Colonial Rule, Caste, Tribes, Classes, Kinship, Religion, Rural Transformation, Urbanization, Politics, Movements, Population, and Challenges.", "Paper II", 2, now);

        // 3. Topics (Flat Architecture)

        // --- PRELIMS GS1 (12 Core Focus Topics) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000001"), prelimsGs1Id, "PGS_ANC_HIST", "Ancient Indian History", "Prehistoric cultures, Indus Valley Civilization, Vedic period, Mahajanapadas, Mauryan Empire, Gupta Empire, and South Indian dynasties.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000002"), prelimsGs1Id, "PGS_MED_HIST", "Medieval Indian History", "Early medieval period, Delhi Sultanate, Vijayanagara Empire, Bhakti and Sufi movements, Mughal Empire, and Marathas.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000003"), prelimsGs1Id, "PGS_MOD_HIST", "Modern Indian History & Freedom Struggle", "Advent of Europeans, British expansion, Revolt of 1857, Socio-religious reform movements, Indian National Congress, Gandhian era, and Independence.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000004"), prelimsGs1Id, "PGS_ART_CUL", "Art & Culture", "Indian architecture, sculpture, paintings, classical dances, music, puppetry, festivals, literature, and UNESCO heritage sites.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000005"), prelimsGs1Id, "PGS_PHY_GEO", "Physical Geography", "Solar system, Earth's interior, plate tectonics, geomorphology, climatology, oceanography, and biogeography.", 1, 5, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000006"), prelimsGs1Id, "PGS_IND_GEO", "Indian Geography", "Physiography of India, drainage systems (Himalayan & Peninsular), Indian monsoon, climate, soil, vegetation, and water resources.", 1, 6, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000007"), prelimsGs1Id, "PGS_HUM_GEO", "Human & Economical Geography", "Population distribution, migration, urbanization, agriculture patterns, mineral & energy resources, industries, and transport networks.", 1, 7, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000008"), prelimsGs1Id, "PGS_POLITY", "Indian Polity & Governance", "Constitutional framework, Fundamental Rights & Duties, Parliament, Executive, Judiciary, Federalism, Panchayati Raj, and Public Policy.", 1, 8, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000009"), prelimsGs1Id, "PGS_ECON", "Indian Economy & Social Development", "National Income, Inflation, Monetary Policy (RBI), Fiscal Policy & Budgeting, Banking, Financial Markets, External Sector, and Poverty Inclusion.", 1, 9, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000010"), prelimsGs1Id, "PGS_ENV", "Ecology & Environment", "Ecosystem concepts, biodiversity conservation (National Parks, Sanctuaries), climate change protocols, environmental conventions, and pollution.", 1, 10, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000011"), prelimsGs1Id, "PGS_SCI", "Science & Technology", "Everyday physics, chemistry, biology; space missions (ISRO), defense technologies, biotechnology, nuclear energy, and IT developments.", 1, 11, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000012"), prelimsGs1Id, "PGS_CURR", "Current Events of National & International Importance", "Major national schemes, international organizations, summit declarations, treaties, bilateral relations, and indices.", 1, 12, now);

        // --- PRELIMS CSAT (Paper II) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000013"), csatId, "CSAT_RC", "Reading Comprehension & Critical Reasoning", "Short and long passages, assumption inference, main idea extraction, and logical consistency.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000014"), csatId, "CSAT_LR", "Logical Reasoning & Analytical Ability", "Deductive syllogisms, blood relations, seating arrangement, direction tests, and series completion.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000015"), csatId, "CSAT_NUM", "Basic Numeracy & Data Interpretation", "Number systems, percentages, profit/loss, ratios, time-speed-distance, permutations, probability, and chart interpretations.", 1, 3, now);

        // --- MAINS ESSAY (Theme Architecture) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000016"), essayId, "ESSAY_EDU", "Education", "Quotes, key statistics, NEP 2020, higher education reforms, skill development, digital learning, and holistic human capital.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000017"), essayId, "ESSAY_WOMEN", "Women", "Gender justice, women's workforce participation, safety, leadership, constitutional safeguards, and socio-economic empowerment.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000018"), essayId, "ESSAY_ENV", "Environment", "Climate change ethics, anthropocentrism vs ecocentrism, renewable energy transition, biodiversity, and sustainable development.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000019"), essayId, "ESSAY_HLTH_SCI", "Health & Science", "Public health infrastructure, health expenditure, bioethics, AI & fourth industrial revolution, human-centric innovation, and scientific temper.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000020"), essayId, "ESSAY_POLITY", "Polity", "Constitutional morality, democracy, federalism, citizen-centric governance, justice, electoral reforms, and democratic institutions.", 1, 5, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000021"), essayId, "ESSAY_PHIL", "Philosophical Themes", "Quotes, moral wisdom, character, human resilience, truth, existential reflections, and philosophical essay frameworks.", 1, 6, now);

        // --- MAINS GS1 (Art & Culture, Modern History, World History, Indian Society, Geography) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000022"), gs1Id, "MGS1_ART", "1. Art & Culture", "Unit 1: Indian culture — Art forms, literature, and architecture from ancient to modern times.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000023"), gs1Id, "MGS1_MOD_HIST", "2. Modern History of India", "Units 2, 3, 4: Significant events, personalities, issues from the middle of the eighteenth century until the present, and the Freedom Struggle stages & contributors.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000024"), gs1Id, "MGS1_WORLD_HIST", "3. Modern History of World", "Unit 5: Events from 18th century such as industrial revolution, world wars, redrawal of national boundaries, colonization, decolonization, and post-independence consolidation.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000025"), gs1Id, "MGS1_SOCIETY", "4. Indian Society", "Units 6, 7, 8, 9: Salient features of Indian Society, diversity, role of women, population issues, poverty, urbanization, effects of globalization, communalism, regionalism & secularism.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000026"), gs1Id, "MGS1_GEO", "5. Geography", "Units 10, 11, 12: Physical geography, Indian geography, Human and economical geography, natural resources distribution, and geophysical phenomena (earthquakes, tsunami, cyclones).", 1, 5, now);

        // --- MAINS GS2 (Indian Polity, Social Justice, Governance, International Relations) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000027"), gs2Id, "MGS2_POLITY", "1. Indian Polity", "Units 1 to 10: Constitution, historical underpinnings, evolution, features, amendments, basic structure, federal structure, separation of powers, Parliament, state legislatures, executive, judiciary, RPA, and constitutional posts.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000028"), gs2Id, "MGS2_SOC_JUST", "2. Social Justice", "Units 11 to 14: Welfare schemes for vulnerable sections, mechanisms, laws, institutions, development & management of health, education, human resources, and issues relating to poverty and hunger.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000029"), gs2Id, "MGS2_GOV", "3. Governance", "Units 15 & 16: Important aspects of governance, transparency, accountability, e-governance, citizens charters, and the role of civil services in a democracy.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000030"), gs2Id, "MGS2_IR", "4. International Relations", "Units 17 to 20: India and its neighborhood relations, bilateral/regional/global groupings, effect of developed/developing countries' policies on India's interests, and international institutions.", 1, 4, now);

        // --- MAINS GS3 (Indian Economy, Science & Tech, Ecology & Environment, Disaster Management, Internal Security) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000031"), gs3Id, "MGS3_ECON", "1. Indian Economy", "Units 1 to 10: Planning, resource mobilization, growth, employment, inclusive growth, government budgeting, cropping patterns, farm subsidies, MSP, PDS, food processing, land reforms, infrastructure, and investment models.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000032"), gs3Id, "MGS3_SCI", "2. Science and Technology", "Units 11 to 13: S&T developments and everyday applications, achievements of Indians in S&T, indigenization, and awareness in IT, Space, Computers, Robotics, Nanotech, Biotech, and IPR.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000033"), gs3Id, "MGS3_ENV", "3. Ecology and Environment", "Unit 14: Conservation, environmental pollution and degradation, and Environmental Impact Assessment (EIA).", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000034"), gs3Id, "MGS3_DM", "4. Disaster Management", "Unit 15: Disaster and disaster management, prevention, preparedness, mitigation, and resilience frameworks.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000035"), gs3Id, "MGS3_SEC", "5. Internal Security", "Units 16 to 20: Linkages between development and extremism, challenges from external state and non-state actors, cyber security, money-laundering prevention, border area management, and security forces & their mandate.", 1, 5, now);

        // --- MAINS GS4 (Ethics, Integrity and Aptitude) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000036"), gs4Id, "MGS4_ETHICS", "Ethics, Integrity and Aptitude", "Ethics and human interface, human values, attitude, foundational values for civil service (integrity, impartiality, objectivity, dedication), emotional intelligence, moral thinkers & philosophers, public service values, probity in governance, RTI, citizen charters, and case studies on ethical dilemmas.", 1, 1, now);

        // --- SOCIOLOGY OPTIONAL PAPER I (Drishti IAS Official Syllabus - 10 Topics) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000037"), socioP1Id, "SOC1_01", "1. Sociology - The Discipline", "(a) Modernity and social changes in Europe and emergence of Sociology. (b) Scope of the subject and comparison with other social sciences. (c) Sociology and common sense.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000038"), socioP1Id, "SOC1_02", "2. Sociology as Science", "(a) Science, scientific method, and critique. (b) Major theoretical strands of research methodology. (c) Positivism and its critique. (d) Fact value and objectivity. (e) Non-positivist methodologies.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000039"), socioP1Id, "SOC1_03", "3. Research Methods and Analysis", "(a) Qualitative and quantitative methods. (b) Techniques of data collection. (c) Variables, sampling, hypothesis, reliability, and validity.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000040"), socioP1Id, "SOC1_04", "4. Sociological Thinkers", "(a) Karl Marx - Historical materialism, mode of production, alienation, class struggle. (b) Emile Durkheim - Division of labour, social fact, suicide, religion and society. (c) Max Weber - Social action, ideal types, authority, bureaucracy, protestant ethic and the spirit of capitalism. (d) Talcott Parsons - Social system, pattern variables. (e) Robert K. Merton - Latent and manifest functions, conformity and deviance, reference groups. (f) Mead - Self and identity.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000041"), socioP1Id, "SOC1_05", "5. Stratification and Mobility", "(a) Concepts - equality, inequality, hierarchy, exclusion, poverty, and deprivation. (b) Theories of social stratification - Structural functionalist theory, Marxist theory, Weberian theory. (c) Dimensions - Social stratification of class, status groups, gender, ethnicity and race. (d) Social mobility - open and closed systems, types of mobility, sources and causes of mobility.", 1, 5, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000042"), socioP1Id, "SOC1_06", "6. Works and Economic Life", "(a) Social organization of work in different types of society - slave society, feudal society, industrial capitalist society. (b) Formal and informal organization of work. (c) Labour and society.", 1, 6, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000043"), socioP1Id, "SOC1_07", "7. Politics and Society", "(a) Sociological theories of power. (b) Power elite, bureaucracy, pressure groups and political parties. (c) Nation, state, citizenship, democracy, civil society, ideology. (d) Protest, agitation, social movements, collective action, revolution.", 1, 7, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000044"), socioP1Id, "SOC1_08", "8. Religion and Society", "(a) Sociological theories of religion. (b) Types of religious practices: animism, monism, pluralism, sects, cults. (c) Religion in modern society: religion and science, secularization, religious revivalism, fundamentalism.", 1, 8, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000045"), socioP1Id, "SOC1_09", "9. Systems of Kinship", "(a) Family, household, marriage. (b) Types and forms of family. (c) Lineage and descent. (d) Patriarchy and sexual division of labour. (e) Contemporary trends.", 1, 9, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000046"), socioP1Id, "SOC1_10", "10. Social Change in Modern Society", "(a) Sociological theories of social change. (b) Development and dependency. (c) Agents of social change. (d) Education and social change. (e) Science, technology, and social change.", 1, 10, now);

        // --- SOCIOLOGY OPTIONAL PAPER II (Drishti IAS Official Syllabus - 15 Topics) ---
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000047"), socioP2Id, "SOC2_01", "1. Perspectives on the Study of Indian Society", "(a) Indology (G.S. Ghurye). (b) Structural functionalism (M.N. Srinivas). (c) Marxist sociology (A.R. Desai).", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000048"), socioP2Id, "SOC2_02", "2. Impact of Colonial Rule on Indian Society", "(a) Social background of Indian nationalism. (b) Modernization of Indian tradition. (c) Protests and movements during the colonial period. (d) Social reforms.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000049"), socioP2Id, "SOC2_03", "3. Rural and Agrarian Social Structure", "(a) The idea of Indian village and village studies. (b) Agrarian social structure — evolution of land tenure system, land reforms.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000050"), socioP2Id, "SOC2_04", "4. Caste System in India", "(a) Perspectives on the study of caste systems: G.S. Ghurye, M.N. Srinivas, Louis Dumont, Andre Beteille. (b) Features of caste system. (c) Untouchability — forms and perspectives.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000051"), socioP2Id, "SOC2_05", "5. Tribal Communities in India", "(a) Definitional problems. (b) Geographical spread. (c) Colonial policies and tribes. (d) Issues of integration and autonomy.", 1, 5, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000052"), socioP2Id, "SOC2_06", "6. Social Classes in India", "(a) Agrarian class structure. (b) Industrial class structure. (c) Middle classes in India.", 1, 6, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000053"), socioP2Id, "SOC2_07", "7. Systems of Kinship in India", "(a) Lineage and descent in India. (b) Types of kinship systems. (c) Family and marriage in India. (d) Household dimensions of the family. (e) Patriarchy, entitlements, and sexual division of labour.", 1, 7, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000054"), socioP2Id, "SOC2_08", "8. Religion and Society in India", "(a) Religious communities in India. (b) Problems of religious minorities.", 1, 8, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000055"), socioP2Id, "SOC2_09", "9. Visions of Social Change in India", "(a) Idea of development planning and mixed economy. (b) Constitution, law, and social change. (c) Education and social change.", 1, 9, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000056"), socioP2Id, "SOC2_10", "10. Rural and Agrarian Transformation in India", "(a) Programmes of rural development, Community Development Programme, cooperatives, poverty alleviation schemes. (b) Green revolution and social change. (c) Changing modes of production in Indian agriculture. (d) Problems of rural labour, bondage, migration.", 1, 10, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000057"), socioP2Id, "SOC2_11", "11. Industrialization and Urbanisation in India", "(a) Evolution of modern industry in India. (b) Growth of urban settlements in India. (c) Working class: structure, growth, class mobilization. (d) Informal sector, child labour. (e) Slums and deprivation in urban areas.", 1, 11, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000058"), socioP2Id, "SOC2_12", "12. Politics and Society in India", "(a) Nation, democracy and citizenship. (b) Political parties, pressure groups, social and political elite. (c) Regionalism and decentralization of power. (d) Secularization.", 1, 12, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000059"), socioP2Id, "SOC2_13", "13. Social Movements in Modern India", "(a) Peasants and farmers' movements. (b) Women’s movement. (c) Backward classes & Dalit movements. (d) Environmental movements. (e) Ethnicity and Identity movements.", 1, 13, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000060"), socioP2Id, "SOC2_14", "14. Population Dynamics in India", "Population size, growth, composition and distribution. Components of population growth: birth, death, migration. Population Policy and family planning. Emerging issues: ageing, sex ratios, child and infant mortality, reproductive health.", 1, 14, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000061"), socioP2Id, "SOC2_15", "15. Challenges of Social Transformation in India", "(a) Crisis of development: displacement, environmental problems and sustainability. (b) Poverty, deprivation and inequalities. (c) Violence against women. (d) Caste conflicts. (e) Ethnic conflicts, communalism, religious revivalism. (f) Illiteracy and disparities in education.", 1, 15, now);

        log.info("UPSC Syllabus updated with exact topics: Prelims (15 topics), Mains (21 topics across Essay & GS1-4), Sociology Optional (25 topics)!");
    }

    private void insertSubject(UUID id, UUID examId, String code, String name, String description, String paper, int order, Timestamp now) {
        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, examId, code, name, description, paper, order, now, now
        );
    }

    private void insertTopic(UUID id, UUID subjectId, String code, String name, String description, int level, int order, Timestamp now) {
        jdbcTemplate.update(
                "INSERT INTO syllabus_topics (id, subject_id, parent_topic_id, code, name, description, level, display_order, created_at, updated_at) VALUES (?, ?, NULL, ?, ?, ?, ?, ?, ?, ?)",
                id, subjectId, code, name, description, level, order, now, now
        );
    }
}
