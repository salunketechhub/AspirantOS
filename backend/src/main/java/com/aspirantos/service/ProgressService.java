package com.aspirantos.service;

import com.aspirantos.dto.progress.OverallProgressResponse;
import com.aspirantos.dto.progress.SubjectProgressResponse;
import com.aspirantos.dto.progress.TopicProgressResponse;
import com.aspirantos.entity.ProgressStatus;

import java.util.Map;
import java.util.UUID;

public interface ProgressService {

    OverallProgressResponse getOverallProgress();

    TopicProgressResponse getTopicProgress(UUID topicId);

    TopicProgressResponse updateTopicProgress(UUID topicId, ProgressStatus status);

    SubjectProgressResponse getSubjectProgress(UUID subjectId);

    Map<UUID, ProgressStatus> getAllTopicProgressMap();
}
