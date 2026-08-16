package com.aspirantos.repository;

import com.aspirantos.entity.ExamStage;
import com.aspirantos.entity.ProgressStatus;
import com.aspirantos.entity.UserTopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTopicProgressRepository extends JpaRepository<UserTopicProgress, UUID> {

    Optional<UserTopicProgress> findByUserIdAndTopicId(UUID userId, UUID topicId);

    List<UserTopicProgress> findByUserId(UUID userId);

    List<UserTopicProgress> findByUserIdAndTopic_SubjectId(UUID userId, UUID subjectId);

    long countByUserIdAndStatus(UUID userId, ProgressStatus status);

    long countByUserIdAndPyqDoneTrue(UUID userId);

    long countByUserIdAndTopic_SubjectIdAndStatus(UUID userId, UUID subjectId, ProgressStatus status);

    long countByUserIdAndTopic_Subject_Exam_StageAndStatus(UUID userId, ExamStage stage, ProgressStatus status);

    long countByUserIdAndTopic_Subject_Exam_StageAndPyqDoneTrue(UUID userId, ExamStage stage);

    void deleteByUserIdAndTopicId(UUID userId, UUID topicId);
}
