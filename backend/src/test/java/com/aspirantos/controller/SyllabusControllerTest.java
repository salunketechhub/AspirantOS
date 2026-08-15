package com.aspirantos.controller;

import com.aspirantos.dto.syllabus.*;
import com.aspirantos.entity.ExamStage;
import com.aspirantos.exception.GlobalExceptionHandler;
import com.aspirantos.exception.ResourceNotFoundException;
import com.aspirantos.service.SyllabusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SyllabusControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SyllabusService syllabusService;

    @InjectMocks
    private SyllabusController syllabusController;

    private UUID examId;
    private UUID subjectId;
    private UUID topicId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(syllabusController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        examId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        topicId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /api/syllabus/exams should return exam list")
    void shouldReturnExamsList() throws Exception {
        ExamResponse exam = ExamResponse.builder()
                .id(examId)
                .code("PRELIMS")
                .name("UPSC Prelims")
                .stage(ExamStage.PRELIMS)
                .displayOrder(1)
                .subjectCount(2)
                .build();

        when(syllabusService.getAllExams()).thenReturn(List.of(exam));

        mockMvc.perform(get("/api/syllabus/exams"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("PRELIMS"))
                .andExpect(jsonPath("$[0].name").value("UPSC Prelims"));
    }

    @Test
    @DisplayName("GET /api/syllabus/exams/{examId} should return exam details")
    void shouldReturnExamById() throws Exception {
        ExamResponse exam = ExamResponse.builder()
                .id(examId)
                .code("PRELIMS")
                .name("UPSC Prelims")
                .stage(ExamStage.PRELIMS)
                .build();

        when(syllabusService.getExamById(examId)).thenReturn(exam);

        mockMvc.perform(get("/api/syllabus/exams/" + examId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PRELIMS"));
    }

    @Test
    @DisplayName("GET /api/syllabus/exams/{examId}/subjects should return subjects for exam")
    void shouldReturnSubjectsByExam() throws Exception {
        SubjectResponse subject = SubjectResponse.builder()
                .id(subjectId)
                .examId(examId)
                .code("PRELIMS_GS1")
                .name("General Studies Paper I")
                .paper("Paper I")
                .build();

        when(syllabusService.getSubjectsByExamId(examId)).thenReturn(List.of(subject));

        mockMvc.perform(get("/api/syllabus/exams/" + examId + "/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("PRELIMS_GS1"));
    }

    @Test
    @DisplayName("GET /api/syllabus/subjects/{subjectId}/topics should return hierarchical topics")
    void shouldReturnTopicTree() throws Exception {
        TopicResponse child = TopicResponse.builder()
                .id(UUID.randomUUID())
                .subjectId(subjectId)
                .parentTopicId(topicId)
                .code("PGS1_POLITY_CONST")
                .name("Constitutional Framework")
                .level(2)
                .build();

        TopicResponse root = TopicResponse.builder()
                .id(topicId)
                .subjectId(subjectId)
                .code("PGS1_POLITY")
                .name("Indian Polity")
                .level(1)
                .subtopics(List.of(child))
                .subtopicCount(1)
                .build();

        when(syllabusService.getTopicTreeBySubjectId(subjectId)).thenReturn(List.of(root));

        mockMvc.perform(get("/api/syllabus/subjects/" + subjectId + "/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("PGS1_POLITY"))
                .andExpect(jsonPath("$[0].subtopics[0].code").value("PGS1_POLITY_CONST"));
    }

    @Test
    @DisplayName("GET /api/syllabus/optionals should return optional catalogue")
    void shouldReturnOptionalsCatalogue() throws Exception {
        OptionalSubjectResponse opt = OptionalSubjectResponse.builder()
                .id(UUID.randomUUID())
                .code("OPT_PSIR")
                .name("PSIR")
                .build();

        when(syllabusService.getAllOptionalSubjects()).thenReturn(List.of(opt));

        mockMvc.perform(get("/api/syllabus/optionals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("OPT_PSIR"));
    }

    @Test
    @DisplayName("GET non-existent exam should return 404 Not Found")
    void shouldReturn404ForNonExistentExam() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(syllabusService.getExamById(unknownId)).thenThrow(new ResourceNotFoundException("Exam not found"));

        mockMvc.perform(get("/api/syllabus/exams/" + unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
