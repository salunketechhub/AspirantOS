package com.aspirantos.service;

import com.aspirantos.dto.progress.OverallProgressResponse;
import com.aspirantos.dto.progress.SubjectProgressResponse;
import com.aspirantos.dto.progress.TopicProgressResponse;
import com.aspirantos.entity.*;
import com.aspirantos.exception.InvalidCredentialsException;
import com.aspirantos.exception.ResourceNotFoundException;
import com.aspirantos.repository.SubjectRepository;
import com.aspirantos.repository.SyllabusTopicRepository;
import com.aspirantos.repository.UserRepository;
import com.aspirantos.repository.UserTopicProgressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProgressServiceImpl implements ProgressService {

    private static final Logger log = LoggerFactory.getLogger(ProgressServiceImpl.class);

    private final UserTopicProgressRepository progressRepository;
    private final SyllabusTopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public ProgressServiceImpl(
            UserTopicProgressRepository progressRepository,
            SyllabusTopicRepository topicRepository,
            SubjectRepository subjectRepository,
            UserRepository userRepository
    ) {
        this.progressRepository = progressRepository;
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public OverallProgressResponse getOverallProgress() {
        User currentUser = getAuthenticatedUser();
        UUID userId = currentUser.getId();

        int totalTopics = (int) topicRepository.count();
        int completedTopics = (int) progressRepository.countByUserIdAndStatus(userId, ProgressStatus.COMPLETED);
        int inProgressTopics = (int) progressRepository.countByUserIdAndStatus(userId, ProgressStatus.IN_PROGRESS);
        int notStartedTopics = Math.max(0, totalTopics - completedTopics - inProgressTopics);

        int completionPercentage = totalTopics > 0
                ? (int) Math.round((double) completedTopics / totalTopics * 100.0)
                : 0;

        int prelimsTotal = (int) topicRepository.countBySubject_Exam_Stage(ExamStage.PRELIMS);
        int prelimsCompleted = (int) progressRepository.countByUserIdAndTopic_Subject_Exam_StageAndStatus(
                userId, ExamStage.PRELIMS, ProgressStatus.COMPLETED
        );
        int prelimsPercentage = prelimsTotal > 0
                ? (int) Math.round((double) prelimsCompleted / prelimsTotal * 100.0)
                : 0;

        int mainsTotal = (int) topicRepository.countBySubject_Exam_Stage(ExamStage.MAINS);
        int mainsCompleted = (int) progressRepository.countByUserIdAndTopic_Subject_Exam_StageAndStatus(
                userId, ExamStage.MAINS, ProgressStatus.COMPLETED
        );
        int mainsPercentage = mainsTotal > 0
                ? (int) Math.round((double) mainsCompleted / mainsTotal * 100.0)
                : 0;

        int optionalTotal = (int) topicRepository.countBySubject_Exam_Stage(ExamStage.OPTIONAL);
        int optionalCompleted = (int) progressRepository.countByUserIdAndTopic_Subject_Exam_StageAndStatus(
                userId, ExamStage.OPTIONAL, ProgressStatus.COMPLETED
        );
        int optionalPercentage = optionalTotal > 0
                ? (int) Math.round((double) optionalCompleted / optionalTotal * 100.0)
                : 0;

        return OverallProgressResponse.builder()
                .totalTopics(totalTopics)
                .completedTopics(completedTopics)
                .inProgressTopics(inProgressTopics)
                .notStartedTopics(notStartedTopics)
                .completionPercentage(completionPercentage)
                .prelimsPercentage(prelimsPercentage)
                .mainsPercentage(mainsPercentage)
                .optionalPercentage(optionalPercentage)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TopicProgressResponse getTopicProgress(UUID topicId) {
        User currentUser = getAuthenticatedUser();
        SyllabusTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus topic not found with ID: " + topicId));

        ProgressStatus status = progressRepository.findByUserIdAndTopicId(currentUser.getId(), topicId)
                .map(UserTopicProgress::getStatus)
                .orElse(ProgressStatus.NOT_STARTED);

        return mapToTopicProgressResponse(topic, status);
    }

    @Override
    @Transactional
    public TopicProgressResponse updateTopicProgress(UUID topicId, ProgressStatus status) {
        User currentUser = getAuthenticatedUser();
        SyllabusTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus topic not found with ID: " + topicId));

        Optional<UserTopicProgress> existing = progressRepository.findByUserIdAndTopicId(currentUser.getId(), topicId);

        if (status == ProgressStatus.NOT_STARTED) {
            existing.ifPresent(progressRepository::delete);
            log.debug("Removed progress record for user {} and topic {} (status: NOT_STARTED)", currentUser.getId(), topicId);
            return mapToTopicProgressResponse(topic, ProgressStatus.NOT_STARTED);
        }

        UserTopicProgress progressRecord;
        if (existing.isPresent()) {
            progressRecord = existing.get();
            progressRecord.setStatus(status);
        } else {
            progressRecord = UserTopicProgress.builder()
                    .user(currentUser)
                    .topic(topic)
                    .status(status)
                    .build();
        }

        progressRepository.save(progressRecord);
        log.debug("Updated progress for user {} on topic {} to {}", currentUser.getId(), topicId, status);

        return mapToTopicProgressResponse(topic, status);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectProgressResponse getSubjectProgress(UUID subjectId) {
        User currentUser = getAuthenticatedUser();
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));

        int totalTopics = (int) topicRepository.countBySubjectId(subjectId);
        int completedTopics = (int) progressRepository.countByUserIdAndTopic_SubjectIdAndStatus(
                currentUser.getId(), subjectId, ProgressStatus.COMPLETED
        );
        int inProgressTopics = (int) progressRepository.countByUserIdAndTopic_SubjectIdAndStatus(
                currentUser.getId(), subjectId, ProgressStatus.IN_PROGRESS
        );
        int notStartedTopics = Math.max(0, totalTopics - completedTopics - inProgressTopics);

        int completionPercentage = totalTopics > 0
                ? (int) Math.round((double) completedTopics / totalTopics * 100.0)
                : 0;

        return SubjectProgressResponse.builder()
                .subjectId(subject.getId())
                .subjectCode(subject.getCode())
                .subjectName(subject.getName())
                .totalTopics(totalTopics)
                .completedTopics(completedTopics)
                .inProgressTopics(inProgressTopics)
                .notStartedTopics(notStartedTopics)
                .completionPercentage(completionPercentage)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, ProgressStatus> getAllTopicProgressMap() {
        User currentUser = getAuthenticatedUser();
        List<UserTopicProgress> userProgressList = progressRepository.findByUserId(currentUser.getId());

        Map<UUID, ProgressStatus> progressMap = new HashMap<>();
        for (UserTopicProgress utp : userProgressList) {
            progressMap.put(utp.getTopic().getId(), utp.getStatus());
        }

        return progressMap;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new InvalidCredentialsException("No authenticated user found in security context");
        }

        String email = authentication.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database"));
    }

    private TopicProgressResponse mapToTopicProgressResponse(SyllabusTopic topic, ProgressStatus status) {
        Subject subject = topic.getSubject();
        return TopicProgressResponse.builder()
                .topicId(topic.getId())
                .topicCode(topic.getCode())
                .topicName(topic.getName())
                .status(status)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectCode(subject != null ? subject.getCode() : null)
                .subjectName(subject != null ? subject.getName() : null)
                .build();
    }
}
