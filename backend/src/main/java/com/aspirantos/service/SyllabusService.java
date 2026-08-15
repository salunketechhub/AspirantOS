package com.aspirantos.service;

import com.aspirantos.dto.syllabus.*;

import java.util.List;
import java.util.UUID;

public interface SyllabusService {

    List<ExamResponse> getAllExams();

    ExamResponse getExamById(UUID examId);

    List<SubjectResponse> getSubjectsByExamId(UUID examId);

    SubjectResponse getSubjectById(UUID subjectId);

    List<TopicResponse> getTopicTreeBySubjectId(UUID subjectId);

    TopicResponse getTopicById(UUID topicId);

    List<OptionalSubjectResponse> getAllOptionalSubjects();

    SyllabusTreeResponse getFullSyllabusTree();
}
