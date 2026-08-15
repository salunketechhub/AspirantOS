package com.aspirantos.service;

import com.aspirantos.dto.syllabus.*;
import com.aspirantos.entity.Exam;
import com.aspirantos.entity.OptionalSubject;
import com.aspirantos.entity.Subject;
import com.aspirantos.entity.SyllabusTopic;
import com.aspirantos.exception.ResourceNotFoundException;
import com.aspirantos.repository.ExamRepository;
import com.aspirantos.repository.OptionalSubjectRepository;
import com.aspirantos.repository.SubjectRepository;
import com.aspirantos.repository.SyllabusTopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SyllabusServiceImpl implements SyllabusService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final SyllabusTopicRepository topicRepository;
    private final OptionalSubjectRepository optionalSubjectRepository;

    public SyllabusServiceImpl(
            ExamRepository examRepository,
            SubjectRepository subjectRepository,
            SyllabusTopicRepository topicRepository,
            OptionalSubjectRepository optionalSubjectRepository
    ) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.optionalSubjectRepository = optionalSubjectRepository;
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapToExamResponse)
                .toList();
    }

    @Override
    public ExamResponse getExamById(UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with ID: " + examId));
        return mapToExamResponse(exam);
    }

    @Override
    public List<SubjectResponse> getSubjectsByExamId(UUID examId) {
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Exam not found with ID: " + examId);
        }
        return subjectRepository.findByExamIdOrderByDisplayOrderAsc(examId).stream()
                .map(this::mapToSubjectResponse)
                .toList();
    }

    @Override
    public SubjectResponse getSubjectById(UUID subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));
        return mapToSubjectResponse(subject);
    }

    @Override
    public List<TopicResponse> getTopicTreeBySubjectId(UUID subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }

        List<SyllabusTopic> allTopics = topicRepository.findBySubjectIdOrderByDisplayOrderAsc(subjectId);
        return buildTopicTree(allTopics);
    }

    @Override
    public TopicResponse getTopicById(UUID topicId) {
        SyllabusTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus topic not found with ID: " + topicId));

        List<SyllabusTopic> directChildren = topicRepository.findByParentTopicIdOrderByDisplayOrderAsc(topicId);
        List<TopicResponse> subtopicResponses = directChildren.stream()
                .map(this::mapToTopicResponseWithoutChildren)
                .toList();

        TopicResponse response = mapToTopicResponseWithoutChildren(topic);
        response.setSubtopics(subtopicResponses);
        response.setSubtopicCount(subtopicResponses.size());
        return response;
    }

    @Override
    public List<OptionalSubjectResponse> getAllOptionalSubjects() {
        return optionalSubjectRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapToOptionalResponse)
                .toList();
    }

    @Override
    public SyllabusTreeResponse getFullSyllabusTree() {
        List<Exam> exams = examRepository.findAllByOrderByDisplayOrderAsc();
        List<OptionalSubject> optionals = optionalSubjectRepository.findAllByOrderByDisplayOrderAsc();

        int totalTopicsCount = 0;
        int totalSubjectsCount = 0;

        List<SyllabusTreeResponse.ExamTreeResponse> examTreeResponses = new ArrayList<>();

        for (Exam exam : exams) {
            List<Subject> subjects = subjectRepository.findByExamIdOrderByDisplayOrderAsc(exam.getId());
            totalSubjectsCount += subjects.size();

            List<SyllabusTreeResponse.SubjectTreeResponse> subjectTreeResponses = new ArrayList<>();

            for (Subject subject : subjects) {
                List<SyllabusTopic> topics = topicRepository.findBySubjectIdOrderByDisplayOrderAsc(subject.getId());
                totalTopicsCount += topics.size();
                List<TopicResponse> topicTree = buildTopicTree(topics);

                subjectTreeResponses.add(SyllabusTreeResponse.SubjectTreeResponse.builder()
                        .id(subject.getId())
                        .code(subject.getCode())
                        .name(subject.getName())
                        .description(subject.getDescription())
                        .paper(subject.getPaper())
                        .displayOrder(subject.getDisplayOrder())
                        .topics(topicTree)
                        .build());
            }

            examTreeResponses.add(SyllabusTreeResponse.ExamTreeResponse.builder()
                    .id(exam.getId())
                    .code(exam.getCode())
                    .name(exam.getName())
                    .description(exam.getDescription())
                    .stage(exam.getStage())
                    .displayOrder(exam.getDisplayOrder())
                    .subjects(subjectTreeResponses)
                    .build());
        }

        List<OptionalSubjectResponse> optionalResponses = optionals.stream()
                .map(this::mapToOptionalResponse)
                .toList();

        return SyllabusTreeResponse.builder()
                .exams(examTreeResponses)
                .optionals(optionalResponses)
                .totalExams(exams.size())
                .totalSubjects(totalSubjectsCount)
                .totalTopics(totalTopicsCount)
                .totalOptionals(optionals.size())
                .build();
    }

    // --- Helper Methods ---

    private ExamResponse mapToExamResponse(Exam exam) {
        int subjectCount = exam.getSubjects() != null ? exam.getSubjects().size() : 0;
        return ExamResponse.builder()
                .id(exam.getId())
                .code(exam.getCode())
                .name(exam.getName())
                .description(exam.getDescription())
                .stage(exam.getStage())
                .displayOrder(exam.getDisplayOrder())
                .subjectCount(subjectCount)
                .build();
    }

    private SubjectResponse mapToSubjectResponse(Subject subject) {
        int topicCount = subject.getTopics() != null ? subject.getTopics().size() : 0;
        return SubjectResponse.builder()
                .id(subject.getId())
                .examId(subject.getExam().getId())
                .examCode(subject.getExam().getCode())
                .examName(subject.getExam().getName())
                .code(subject.getCode())
                .name(subject.getName())
                .description(subject.getDescription())
                .paper(subject.getPaper())
                .displayOrder(subject.getDisplayOrder())
                .topicCount(topicCount)
                .build();
    }

    private TopicResponse mapToTopicResponseWithoutChildren(SyllabusTopic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .subjectId(topic.getSubject().getId())
                .subjectCode(topic.getSubject().getCode())
                .subjectName(topic.getSubject().getName())
                .parentTopicId(topic.getParentTopic() != null ? topic.getParentTopic().getId() : null)
                .code(topic.getCode())
                .name(topic.getName())
                .description(topic.getDescription())
                .level(topic.getLevel())
                .displayOrder(topic.getDisplayOrder())
                .subtopics(new ArrayList<>())
                .subtopicCount(0)
                .build();
    }

    private OptionalSubjectResponse mapToOptionalResponse(OptionalSubject optional) {
        return OptionalSubjectResponse.builder()
                .id(optional.getId())
                .code(optional.getCode())
                .name(optional.getName())
                .description(optional.getDescription())
                .displayOrder(optional.getDisplayOrder())
                .build();
    }

    private List<TopicResponse> buildTopicTree(List<SyllabusTopic> allTopics) {
        if (allTopics == null || allTopics.isEmpty()) {
            return Collections.emptyList();
        }

        // Group topics by parent ID
        Map<UUID, List<SyllabusTopic>> childrenMap = new HashMap<>();
        List<SyllabusTopic> rootTopics = new ArrayList<>();

        for (SyllabusTopic topic : allTopics) {
            if (topic.getParentTopic() == null) {
                rootTopics.add(topic);
            } else {
                childrenMap.computeIfAbsent(topic.getParentTopic().getId(), k -> new ArrayList<>()).add(topic);
            }
        }

        return rootTopics.stream()
                .map(root -> buildNode(root, childrenMap))
                .collect(Collectors.toList());
    }

    private TopicResponse buildNode(SyllabusTopic topic, Map<UUID, List<SyllabusTopic>> childrenMap) {
        TopicResponse response = mapToTopicResponseWithoutChildren(topic);
        List<SyllabusTopic> children = childrenMap.getOrDefault(topic.getId(), Collections.emptyList());

        List<TopicResponse> childResponses = children.stream()
                .map(child -> buildNode(child, childrenMap))
                .collect(Collectors.toList());

        response.setSubtopics(childResponses);
        response.setSubtopicCount(childResponses.size());
        return response;
    }
}
