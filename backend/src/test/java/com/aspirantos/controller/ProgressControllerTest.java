package com.aspirantos.controller;

import com.aspirantos.dto.progress.OverallProgressResponse;
import com.aspirantos.dto.progress.SubjectProgressResponse;
import com.aspirantos.dto.progress.TopicProgressResponse;
import com.aspirantos.entity.ProgressStatus;
import com.aspirantos.exception.GlobalExceptionHandler;
import com.aspirantos.exception.ResourceNotFoundException;
import com.aspirantos.service.ProgressService;
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

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProgressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProgressService progressService;

    @InjectMocks
    private ProgressController progressController;

    private UUID topicId;
    private UUID subjectId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(progressController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        topicId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /api/progress should return overall user progress")
    void shouldReturnOverallProgress() throws Exception {
        OverallProgressResponse overall = OverallProgressResponse.builder()
                .totalTopics(35)
                .completedTopics(12)
                .inProgressTopics(5)
                .notStartedTopics(18)
                .completionPercentage(34)
                .prelimsPercentage(42)
                .mainsPercentage(28)
                .optionalPercentage(0)
                .build();

        when(progressService.getOverallProgress()).thenReturn(overall);

        mockMvc.perform(get("/api/progress"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalTopics").value(35))
                .andExpect(jsonPath("$.completedTopics").value(12))
                .andExpect(jsonPath("$.completionPercentage").value(34))
                .andExpect(jsonPath("$.prelimsPercentage").value(42));
    }

    @Test
    @DisplayName("GET /api/progress/topics/{topicId} should return topic progress")
    void shouldReturnTopicProgress() throws Exception {
        TopicProgressResponse topicProgress = TopicProgressResponse.builder()
                .topicId(topicId)
                .topicCode("PGS1_POLITY")
                .topicName("Indian Polity")
                .status(ProgressStatus.COMPLETED)
                .subjectId(subjectId)
                .subjectCode("PRELIMS_GS1")
                .subjectName("General Studies Paper I")
                .build();

        when(progressService.getTopicProgress(topicId)).thenReturn(topicProgress);

        mockMvc.perform(get("/api/progress/topics/" + topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicCode").value("PGS1_POLITY"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("PUT /api/progress/topics/{topicId} should update and return new status")
    void shouldUpdateTopicProgress() throws Exception {
        TopicProgressResponse updated = TopicProgressResponse.builder()
                .topicId(topicId)
                .topicCode("PGS1_POLITY")
                .topicName("Indian Polity")
                .status(ProgressStatus.IN_PROGRESS)
                .subjectId(subjectId)
                .build();

        when(progressService.updateTopicProgress(topicId, ProgressStatus.IN_PROGRESS)).thenReturn(updated);

        mockMvc.perform(put("/api/progress/topics/" + topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("GET /api/progress/subjects/{subjectId} should return subject progress")
    void shouldReturnSubjectProgress() throws Exception {
        SubjectProgressResponse subjectProgress = SubjectProgressResponse.builder()
                .subjectId(subjectId)
                .subjectCode("PRELIMS_GS1")
                .subjectName("General Studies Paper I")
                .totalTopics(20)
                .completedTopics(10)
                .inProgressTopics(3)
                .notStartedTopics(7)
                .completionPercentage(50)
                .build();

        when(progressService.getSubjectProgress(subjectId)).thenReturn(subjectProgress);

        mockMvc.perform(get("/api/progress/subjects/" + subjectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectCode").value("PRELIMS_GS1"))
                .andExpect(jsonPath("$.completionPercentage").value(50));
    }

    @Test
    @DisplayName("PUT /api/progress/topics/{topicId} with invalid status should return 400 Bad Request")
    void shouldReturn400ForInvalidStatus() throws Exception {
        mockMvc.perform(put("/api/progress/topics/" + topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"INVALID_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/progress/topics/{topicId} with non-existent topic should return 404 Not Found")
    void shouldReturn404ForNonExistentTopic() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(progressService.getTopicProgress(unknownId))
                .thenThrow(new ResourceNotFoundException("Syllabus topic not found with ID: " + unknownId));

        mockMvc.perform(get("/api/progress/topics/" + unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
