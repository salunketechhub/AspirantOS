package com.aspirantos.service;

import com.aspirantos.dto.progress.OverallProgressResponse;
import com.aspirantos.dto.progress.SubjectProgressResponse;
import com.aspirantos.dto.progress.TopicProgressResponse;
import com.aspirantos.entity.*;
import com.aspirantos.exception.ResourceNotFoundException;
import com.aspirantos.repository.SubjectRepository;
import com.aspirantos.repository.SyllabusTopicRepository;
import com.aspirantos.repository.UserRepository;
import com.aspirantos.repository.UserTopicProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private UserTopicProgressRepository progressRepository;

    @Mock
    private SyllabusTopicRepository topicRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private User testUser;
    private Exam testExam;
    private Subject testSubject;
    private SyllabusTopic testTopic;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        testExam = Exam.builder()
                .id(UUID.randomUUID())
                .code("PRELIMS")
                .name("UPSC Prelims")
                .stage(ExamStage.PRELIMS)
                .build();

        testSubject = Subject.builder()
                .id(UUID.randomUUID())
                .exam(testExam)
                .code("PRELIMS_GS1")
                .name("General Studies Paper I")
                .paper("Paper I")
                .build();

        testTopic = SyllabusTopic.builder()
                .id(UUID.randomUUID())
                .subject(testSubject)
                .code("PGS1_POLITY")
                .name("Indian Polity")
                .level(1)
                .build();

        // Setup mock authenticated security context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser.getEmail(), null, testUser.getAuthorities())
        );
    }

    @Test
    @DisplayName("Should default unrecorded topic to NOT_STARTED")
    void shouldDefaultUnrecordedTopicToNotStarted() {
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(testTopic.getId())).thenReturn(Optional.of(testTopic));
        when(progressRepository.findByUserIdAndTopicId(testUser.getId(), testTopic.getId())).thenReturn(Optional.empty());

        TopicProgressResponse response = progressService.getTopicProgress(testTopic.getId());

        assertNotNull(response);
        assertEquals(testTopic.getId(), response.getTopicId());
        assertEquals(ProgressStatus.NOT_STARTED, response.getStatus());
        assertEquals("PGS1_POLITY", response.getTopicCode());
    }

    @Test
    @DisplayName("Should update topic to IN_PROGRESS and save progress record")
    void shouldUpdateTopicToInProgress() {
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(testTopic.getId())).thenReturn(Optional.of(testTopic));
        when(progressRepository.findByUserIdAndTopicId(testUser.getId(), testTopic.getId())).thenReturn(Optional.empty());

        TopicProgressResponse response = progressService.updateTopicProgress(testTopic.getId(), ProgressStatus.IN_PROGRESS);

        assertNotNull(response);
        assertEquals(ProgressStatus.IN_PROGRESS, response.getStatus());
        verify(progressRepository, times(1)).save(any(UserTopicProgress.class));
    }

    @Test
    @DisplayName("Should update topic to COMPLETED and save progress record")
    void shouldUpdateTopicToCompleted() {
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(testTopic.getId())).thenReturn(Optional.of(testTopic));
        when(progressRepository.findByUserIdAndTopicId(testUser.getId(), testTopic.getId())).thenReturn(Optional.empty());

        TopicProgressResponse response = progressService.updateTopicProgress(testTopic.getId(), ProgressStatus.COMPLETED);

        assertNotNull(response);
        assertEquals(ProgressStatus.COMPLETED, response.getStatus());
        verify(progressRepository, times(1)).save(any(UserTopicProgress.class));
    }

    @Test
    @DisplayName("Should delete progress row when updated back to NOT_STARTED")
    void shouldDeleteProgressWhenSetToNotStarted() {
        UserTopicProgress existing = UserTopicProgress.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .topic(testTopic)
                .status(ProgressStatus.IN_PROGRESS)
                .build();

        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(testTopic.getId())).thenReturn(Optional.of(testTopic));
        when(progressRepository.findByUserIdAndTopicId(testUser.getId(), testTopic.getId())).thenReturn(Optional.of(existing));

        TopicProgressResponse response = progressService.updateTopicProgress(testTopic.getId(), ProgressStatus.NOT_STARTED);

        assertNotNull(response);
        assertEquals(ProgressStatus.NOT_STARTED, response.getStatus());
        verify(progressRepository, times(1)).delete(existing);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent topic")
    void shouldThrowExceptionForNonExistentTopic() {
        UUID unknownTopicId = UUID.randomUUID();
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(unknownTopicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> progressService.getTopicProgress(unknownTopicId));
        assertThrows(ResourceNotFoundException.class, () -> progressService.updateTopicProgress(unknownTopicId, ProgressStatus.COMPLETED));
    }

    @Test
    @DisplayName("Should calculate overall completion percentages correctly")
    void shouldCalculateOverallProgressCorrectly() {
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(topicRepository.count()).thenReturn(20L);
        when(progressRepository.countByUserIdAndStatus(testUser.getId(), ProgressStatus.COMPLETED)).thenReturn(8L);
        when(progressRepository.countByUserIdAndStatus(testUser.getId(), ProgressStatus.IN_PROGRESS)).thenReturn(4L);

        when(topicRepository.countBySubject_Exam_Stage(ExamStage.PRELIMS)).thenReturn(10L);
        when(progressRepository.countByUserIdAndTopic_Subject_Exam_StageAndStatus(testUser.getId(), ExamStage.PRELIMS, ProgressStatus.COMPLETED)).thenReturn(6L);

        when(topicRepository.countBySubject_Exam_Stage(ExamStage.MAINS)).thenReturn(10L);
        when(progressRepository.countByUserIdAndTopic_Subject_Exam_StageAndStatus(testUser.getId(), ExamStage.MAINS, ProgressStatus.COMPLETED)).thenReturn(2L);

        when(topicRepository.countBySubject_Exam_Stage(ExamStage.OPTIONAL)).thenReturn(5L);
        when(progressRepository.countByUserIdAndTopic_Subject_Exam_StageAndStatus(testUser.getId(), ExamStage.OPTIONAL, ProgressStatus.COMPLETED)).thenReturn(4L);

        OverallProgressResponse progress = progressService.getOverallProgress();

        assertNotNull(progress);
        assertEquals(20, progress.getTotalTopics());
        assertEquals(8, progress.getCompletedTopics());
        assertEquals(4, progress.getInProgressTopics());
        assertEquals(8, progress.getNotStartedTopics());
        assertEquals(40, progress.getCompletionPercentage(), "8/20 should be 40%");
        assertEquals(60, progress.getPrelimsPercentage(), "6/10 should be 60%");
        assertEquals(20, progress.getMainsPercentage(), "2/10 should be 20%");
        assertEquals(80, progress.getOptionalPercentage(), "4/5 should be 80%");
    }

    @Test
    @DisplayName("Should calculate subject progress correctly")
    void shouldCalculateSubjectProgressCorrectly() {
        UUID subjectId = testSubject.getId();
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(testSubject));
        when(topicRepository.countBySubjectId(subjectId)).thenReturn(10L);
        when(progressRepository.countByUserIdAndTopic_SubjectIdAndStatus(testUser.getId(), subjectId, ProgressStatus.COMPLETED)).thenReturn(5L);
        when(progressRepository.countByUserIdAndTopic_SubjectIdAndStatus(testUser.getId(), subjectId, ProgressStatus.IN_PROGRESS)).thenReturn(2L);

        SubjectProgressResponse response = progressService.getSubjectProgress(subjectId);

        assertNotNull(response);
        assertEquals(subjectId, response.getSubjectId());
        assertEquals(10, response.getTotalTopics());
        assertEquals(5, response.getCompletedTopics());
        assertEquals(2, response.getInProgressTopics());
        assertEquals(3, response.getNotStartedTopics());
        assertEquals(50, response.getCompletionPercentage(), "5/10 should be 50%");
    }
}
