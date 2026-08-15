package com.aspirantos.service;

import com.aspirantos.dto.syllabus.*;
import com.aspirantos.entity.Exam;
import com.aspirantos.entity.ExamStage;
import com.aspirantos.entity.OptionalSubject;
import com.aspirantos.entity.Subject;
import com.aspirantos.entity.SyllabusTopic;
import com.aspirantos.exception.ResourceNotFoundException;
import com.aspirantos.repository.ExamRepository;
import com.aspirantos.repository.OptionalSubjectRepository;
import com.aspirantos.repository.SubjectRepository;
import com.aspirantos.repository.SyllabusTopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyllabusServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SyllabusTopicRepository topicRepository;

    @Mock
    private OptionalSubjectRepository optionalSubjectRepository;

    @InjectMocks
    private SyllabusServiceImpl syllabusService;

    private Exam testExam;
    private Subject testSubject;
    private SyllabusTopic rootTopic;
    private SyllabusTopic childTopic;
    private OptionalSubject testOptional;

    @BeforeEach
    void setUp() {
        testExam = Exam.builder()
                .id(UUID.randomUUID())
                .code("PRELIMS")
                .name("UPSC Prelims")
                .description("Preliminary test")
                .stage(ExamStage.PRELIMS)
                .displayOrder(1)
                .subjects(new ArrayList<>())
                .build();

        testSubject = Subject.builder()
                .id(UUID.randomUUID())
                .exam(testExam)
                .code("PRELIMS_GS1")
                .name("General Studies Paper I")
                .description("GS Paper 1")
                .paper("Paper I")
                .displayOrder(1)
                .topics(new ArrayList<>())
                .build();

        rootTopic = SyllabusTopic.builder()
                .id(UUID.randomUUID())
                .subject(testSubject)
                .parentTopic(null)
                .code("PGS1_POLITY")
                .name("Indian Polity & Governance")
                .description("Polity")
                .level(1)
                .displayOrder(1)
                .build();

        childTopic = SyllabusTopic.builder()
                .id(UUID.randomUUID())
                .subject(testSubject)
                .parentTopic(rootTopic)
                .code("PGS1_POLITY_CONST")
                .name("Constitutional Framework")
                .description("Constitution")
                .level(2)
                .displayOrder(1)
                .build();

        testOptional = OptionalSubject.builder()
                .id(UUID.randomUUID())
                .code("OPT_PSIR")
                .name("PSIR")
                .description("Political Science")
                .displayOrder(1)
                .build();
    }

    @Test
    @DisplayName("Should return all exams ordered by display order")
    void shouldReturnAllExams() {
        when(examRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(testExam));

        List<ExamResponse> responses = syllabusService.getAllExams();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("PRELIMS", responses.get(0).getCode());
        verify(examRepository, times(1)).findAllByOrderByDisplayOrderAsc();
    }

    @Test
    @DisplayName("Should return exam by ID or throw exception")
    void shouldReturnExamByIdOrThrow() {
        UUID id = testExam.getId();
        when(examRepository.findById(id)).thenReturn(Optional.of(testExam));

        ExamResponse response = syllabusService.getExamById(id);
        assertNotNull(response);
        assertEquals("PRELIMS", response.getCode());

        UUID unknownId = UUID.randomUUID();
        when(examRepository.findById(unknownId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> syllabusService.getExamById(unknownId));
    }

    @Test
    @DisplayName("Should return subjects by exam ID")
    void shouldReturnSubjectsByExamId() {
        UUID examId = testExam.getId();
        when(examRepository.existsById(examId)).thenReturn(true);
        when(subjectRepository.findByExamIdOrderByDisplayOrderAsc(examId)).thenReturn(List.of(testSubject));

        List<SubjectResponse> subjects = syllabusService.getSubjectsByExamId(examId);

        assertNotNull(subjects);
        assertEquals(1, subjects.size());
        assertEquals("PRELIMS_GS1", subjects.get(0).getCode());
    }

    @Test
    @DisplayName("Should assemble hierarchical topic tree with nested subtopics")
    void shouldAssembleHierarchicalTopicTree() {
        UUID subjectId = testSubject.getId();
        when(subjectRepository.existsById(subjectId)).thenReturn(true);
        when(topicRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId)).thenReturn(List.of(rootTopic, childTopic));

        List<TopicResponse> tree = syllabusService.getTopicTreeBySubjectId(subjectId);

        assertNotNull(tree);
        assertEquals(1, tree.size(), "Should contain 1 root topic");
        TopicResponse root = tree.get(0);
        assertEquals("PGS1_POLITY", root.getCode());
        assertEquals(1, root.getLevel());
        assertEquals(1, root.getSubtopics().size(), "Root topic should have 1 subtopic");
        assertEquals("PGS1_POLITY_CONST", root.getSubtopics().get(0).getCode());
        assertEquals(2, root.getSubtopics().get(0).getLevel());
    }

    @Test
    @DisplayName("Should return all optional subjects")
    void shouldReturnAllOptionalSubjects() {
        when(optionalSubjectRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(testOptional));

        List<OptionalSubjectResponse> optionals = syllabusService.getAllOptionalSubjects();

        assertNotNull(optionals);
        assertEquals(1, optionals.size());
        assertEquals("OPT_PSIR", optionals.get(0).getCode());
    }
}
