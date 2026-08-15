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
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM exams", Integer.class);
        if (count != null && count > 0) {
            log.info("Syllabus data already exists in database ({} exams found). Skipping seeder.", count);
            return;
        }

        log.info("Seeding initial UPSC Syllabus & Subject Architecture data via JDBC...");
        Timestamp now = Timestamp.from(Instant.now());

        // 1. Exams
        UUID prelimsId = UUID.fromString("a0000000-0000-0000-0000-000000000001");
        UUID mainsId = UUID.fromString("a0000000-0000-0000-0000-000000000002");

        jdbcTemplate.update(
                "INSERT INTO exams (id, code, name, description, stage, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                prelimsId, "PRELIMS", "UPSC Civil Services (Preliminary) Examination",
                "Objective screening test consisting of General Studies Paper I and CSAT Paper II.",
                "PRELIMS", 1, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO exams (id, code, name, description, stage, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                mainsId, "MAINS", "UPSC Civil Services (Main) Examination",
                "Written descriptive test consisting of Essay, 4 General Studies papers, and Optional papers.",
                "MAINS", 2, now, now
        );

        // 2. Prelims Subjects
        UUID prelimsGs1Id = UUID.fromString("b0000000-0000-0000-0000-000000000001");
        UUID csatId = UUID.fromString("b0000000-0000-0000-0000-000000000002");

        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                prelimsGs1Id, prelimsId, "PRELIMS_GS1", "General Studies Paper I",
                "Current events, History of India, Geography, Polity, Governance, Economic Development, Environmental Ecology, and General Science.",
                "Paper I", 1, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                csatId, prelimsId, "PRELIMS_CSAT", "CSAT / Paper II",
                "Comprehension, interpersonal skills, logical reasoning, analytical ability, decision-making, and basic numeracy.",
                "Paper II", 2, now, now
        );

        // 3. Mains Subjects
        UUID essayId = UUID.fromString("b0000000-0000-0000-0000-000000000003");
        UUID mainsGs1Id = UUID.fromString("b0000000-0000-0000-0000-000000000004");
        UUID mainsGs2Id = UUID.fromString("b0000000-0000-0000-0000-000000000005");
        UUID mainsGs3Id = UUID.fromString("b0000000-0000-0000-0000-000000000006");
        UUID mainsGs4Id = UUID.fromString("b0000000-0000-0000-0000-000000000007");

        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                essayId, mainsId, "MAINS_ESSAY", "Essay",
                "Candidates are required to write essays on multiple philosophical, socio-economic, and administrative themes.",
                "Essay", 1, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                mainsGs1Id, mainsId, "MAINS_GS1", "General Studies I",
                "Indian Heritage and Culture, History and Geography of the World and Society.",
                "GS Paper I", 2, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                mainsGs2Id, mainsId, "MAINS_GS2", "General Studies II",
                "Governance, Constitution, Polity, Social Justice and International Relations.",
                "GS Paper II", 3, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                mainsGs3Id, mainsId, "MAINS_GS3", "General Studies III",
                "Technology, Economic Development, Biodiversity, Environment, Security and Disaster Management.",
                "GS Paper III", 4, now, now
        );

        jdbcTemplate.update(
                "INSERT INTO subjects (id, exam_id, code, name, description, paper, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                mainsGs4Id, mainsId, "MAINS_GS4", "General Studies IV",
                "Ethics, Integrity, and Aptitude.",
                "GS Paper IV", 5, now, now
        );

        // 4. Prelims GS1 Topics
        UUID t1 = UUID.fromString("c0000000-0000-0000-0000-000000000001");
        UUID t2 = UUID.fromString("c0000000-0000-0000-0000-000000000002");
        UUID t3 = UUID.fromString("c0000000-0000-0000-0000-000000000003");
        UUID t4 = UUID.fromString("c0000000-0000-0000-0000-000000000004");
        UUID t5 = UUID.fromString("c0000000-0000-0000-0000-000000000005");
        UUID t6 = UUID.fromString("c0000000-0000-0000-0000-000000000006");
        UUID t7 = UUID.fromString("c0000000-0000-0000-0000-000000000007");
        UUID t8 = UUID.fromString("c0000000-0000-0000-0000-000000000008");
        UUID t9 = UUID.fromString("c0000000-0000-0000-0000-000000000009");
        UUID t10 = UUID.fromString("c0000000-0000-0000-0000-000000000010");
        UUID t11 = UUID.fromString("c0000000-0000-0000-0000-000000000011");
        UUID t12 = UUID.fromString("c0000000-0000-0000-0000-000000000012");
        UUID t13 = UUID.fromString("c0000000-0000-0000-0000-000000000013");

        insertTopic(t1, prelimsGs1Id, null, "PGS1_POLITY", "Indian Polity & Governance", "Constitution, Political System, Panchayati Raj, Public Policy, Rights Issues, etc.", 1, 1, now);
        insertTopic(t2, prelimsGs1Id, t1, "PGS1_POLITY_CONST", "Constitutional Framework & Organs of State", "Preamble, Fundamental Rights, DPSP, Parliament, Executive, and Judiciary.", 2, 1, now);
        insertTopic(t3, prelimsGs1Id, t1, "PGS1_POLITY_LOCAL", "Panchayati Raj & Local Governance", "73rd and 74th Constitutional Amendment Acts, Urban Local Bodies, Decentralization.", 2, 2, now);

        insertTopic(t4, prelimsGs1Id, null, "PGS1_HIST", "History of India & Indian National Movement", "Ancient, Medieval, and Modern Indian history with focus on freedom struggle.", 1, 2, now);
        insertTopic(t5, prelimsGs1Id, t4, "PGS1_HIST_ANC_MED", "Ancient & Medieval India", "Indus Valley, Vedic Age, Mauryan, Gupta, Delhi Sultanate, and Mughal Empire.", 2, 1, now);
        insertTopic(t6, prelimsGs1Id, t4, "PGS1_HIST_MODERN", "Modern Indian History & National Movement", "British expansion, 1857 Revolt, INC, Gandhian Era, and partition/independence.", 2, 2, now);

        insertTopic(t7, prelimsGs1Id, null, "PGS1_GEO", "Indian and World Geography", "Physical, Social, and Economic Geography of India and the World.", 1, 3, now);
        insertTopic(t8, prelimsGs1Id, t7, "PGS1_GEO_PHYSICAL", "Physical Geography & Geomorphology", "Earth structure, plate tectonics, climatology, oceanography, Indian physiography.", 2, 1, now);
        insertTopic(t9, prelimsGs1Id, t7, "PGS1_GEO_RESOURCES", "Economic Geography & Resources", "Agriculture, minerals, industries, infrastructure, energy resources.", 2, 2, now);

        insertTopic(t10, prelimsGs1Id, null, "PGS1_ECON", "Economic and Social Development", "Sustainable Development, Poverty, Inclusion, Demographics, Social Sector Initiatives.", 1, 4, now);
        insertTopic(t11, prelimsGs1Id, t10, "PGS1_ECON_MACRO", "Macroeconomics, Fiscal & Monetary Policy", "National income accounting, inflation, RBI policies, banking, taxation, and budget.", 2, 1, now);

        insertTopic(t12, prelimsGs1Id, null, "PGS1_ENV", "Environmental Ecology, Biodiversity & Climate Change", "General issues that do not require subject specialization.", 1, 5, now);
        insertTopic(t13, prelimsGs1Id, t12, "PGS1_ENV_BIODIV", "Biodiversity & Conservation Efforts", "Protected area networks, IUCN status, wildlife acts, Ramsar wetlands.", 2, 1, now);

        // 5. CSAT Topics
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000014"), csatId, null, "CSAT_RC", "Reading Comprehension", "Short and long passages testing critical comprehension and inference skills.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000015"), csatId, null, "CSAT_LR", "Logical Reasoning & Analytical Ability", "Syllogisms, seating arrangement, coding-decoding, blood relations, and puzzles.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000016"), csatId, null, "CSAT_QA", "Quantitative Aptitude & Basic Numeracy", "Numbers, percentage, profit & loss, ratio, permutations, and data interpretation.", 1, 3, now);

        // 6. Mains GS1 Topics
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000017"), mainsGs1Id, null, "MGS1_ART", "Indian Art, Architecture & Culture", "Salient aspects of Art Forms, Literature and Architecture from ancient to modern times.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000018"), mainsGs1Id, null, "MGS1_MOD_HIST", "Modern Indian History & Freedom Struggle", "Significant events, personalities, and stages of the freedom movement.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000019"), mainsGs1Id, null, "MGS1_WORLD_HIST", "History of the World", "Events from 18th century such as Industrial Revolution, World Wars, Decolonization, and Political Philosophies.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000020"), mainsGs1Id, null, "MGS1_SOCIETY", "Salient Features of Indian Society & Diversity", "Role of women, population issues, poverty, urbanization, effects of globalization, communalism, regionalism, secularism.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000021"), mainsGs1Id, null, "MGS1_GEO", "World & Indian Physical Geography", "Distribution of key natural resources, factors responsible for industrial locations, geophysical phenomena.", 1, 5, now);

        // 7. Mains GS2 Topics
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000022"), mainsGs2Id, null, "MGS2_CONST", "Indian Constitution & Federalism", "Evolution, amendments, significant provisions, basic structure, federal issues, separation of powers.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000023"), mainsGs2Id, null, "MGS2_GOV", "Governance, Transparency & Accountability", "E-governance, citizen charters, role of civil services, statutory, regulatory and quasi-judicial bodies.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000024"), mainsGs2Id, null, "MGS2_SOC_JUST", "Social Justice & Welfare Initiatives", "Welfare schemes for vulnerable sections, health, education, human resources, issues relating to poverty and hunger.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000025"), mainsGs2Id, null, "MGS2_IR", "International Relations & Global Groupings", "India and its neighborhood, bilateral, regional and global groupings, effect of policies of developed/developing nations.", 1, 4, now);

        // 8. Mains GS3 Topics
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000026"), mainsGs3Id, null, "MGS3_ECON", "Indian Economy & Inclusive Growth", "Planning, mobilization of resources, growth, development, employment, and government budgeting.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000027"), mainsGs3Id, null, "MGS3_AGRI", "Agriculture & Food Processing", "Major crops, cropping patterns, irrigation, direct & indirect farm subsidies, MSP, PDS, economics of animal-rearing.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000028"), mainsGs3Id, null, "MGS3_SCI_TECH", "Science & Technology Developments", "Indigenization of technology, IT, space, computers, robotics, nanotechnology, biotechnology, and IPR.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000029"), mainsGs3Id, null, "MGS3_ENV_DM", "Environment Conservation & Disaster Management", "Pollution, degradation, environmental impact assessment, disaster and disaster management mechanisms.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000030"), mainsGs3Id, null, "MGS3_SEC", "Internal Security & Border Management", "Linkages between development and spread of extremism, cyber security, money laundering, security forces.", 1, 5, now);

        // 9. Mains GS4 Topics
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000031"), mainsGs4Id, null, "MGS4_ETHICS", "Ethics and Human Interface", "Essence, determinants and consequences of Ethics in human actions; dimensions of ethics; ethics in private and public relationships.", 1, 1, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000032"), mainsGs4Id, null, "MGS4_HUMAN_VAL", "Human Values & Thinkers", "Lessons from the lives and teachings of great leaders, reformers and administrators; role of family, society and educational institutions.", 1, 2, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000033"), mainsGs4Id, null, "MGS4_ATTITUDE", "Attitude & Emotional Intelligence", "Content, structure, function; its influence and relation with thought and behaviour; moral and political attitudes; social influence and persuasion.", 1, 3, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000034"), mainsGs4Id, null, "MGS4_PROBITY", "Probity in Governance & Public Service Values", "Concept of public service; philosophical basis of governance and probity; information sharing, transparency, RTI, codes of ethics.", 1, 4, now);
        insertTopic(UUID.fromString("c0000000-0000-0000-0000-000000000035"), mainsGs4Id, null, "MGS4_CASES", "Case Studies on Ethical Dilemmas", "Practical scenario case studies on issues involving corruption, administrative ethics, and public welfare.", 1, 5, now);

        // 10. Optional Subjects Catalogue
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000001"), "OPT_PUB_AD", "Public Administration", "Administrative theory, Indian administration, public policy, financial administration, and rural-urban development.", 1, now);
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000002"), "OPT_PSIR", "Political Science & International Relations", "Political theory, Indian nationalism, comparative politics, and India’s foreign policy.", 2, now);
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000003"), "OPT_SOCIO", "Sociology", "Sociological thinkers, social stratification, politics and society, religion, family, and social change in India.", 3, now);
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000004"), "OPT_GEO", "Geography", "Geomorphology, climatology, oceanography, population geography, regional planning, and geography of India.", 4, now);
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000005"), "OPT_HIST", "History", "Sources, early Indian society, medieval political formations, colonial rule, and world history.", 5, now);
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000006"), "OPT_ANTHRO", "Anthropology", "Physical anthropology, sociocultural anthropology, Indian anthropology, and tribal communities.", 6, now);
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000007"), "OPT_ECON", "Economics", "Advanced micro & macroeconomics, public finance, international economics, growth & development, and Indian economy.", 7, now);
        insertOptional(UUID.fromString("d0000000-0000-0000-0000-000000000008"), "OPT_PHIL", "Philosophy", "Western philosophy, Indian philosophy, socio-political philosophy, and philosophy of religion.", 8, now);

        log.info("UPSC Syllabus & Subject Architecture seeded successfully via JDBC (2 exams, 7 subjects, 35 topics, 8 optionals).");
    }

    private void insertTopic(UUID id, UUID subjectId, UUID parentTopicId, String code, String name, String description, int level, int displayOrder, Timestamp timestamp) {
        jdbcTemplate.update(
                "INSERT INTO syllabus_topics (id, subject_id, parent_topic_id, code, name, description, level, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, subjectId, parentTopicId, code, name, description, level, displayOrder, timestamp, timestamp
        );
    }

    private void insertOptional(UUID id, String code, String name, String description, int displayOrder, Timestamp timestamp) {
        jdbcTemplate.update(
                "INSERT INTO optional_subjects (id, code, name, description, display_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                id, code, name, description, displayOrder, timestamp, timestamp
        );
    }
}
