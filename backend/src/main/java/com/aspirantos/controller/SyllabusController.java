package com.aspirantos.controller;

import com.aspirantos.dto.syllabus.*;
import com.aspirantos.service.SyllabusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/syllabus")
@Tag(name = "Syllabus Architecture", description = "Endpoints for exploring the UPSC syllabus, exams, subjects, topic hierarchy, and optional catalogue")
@SecurityRequirement(name = "BearerAuth")
public class SyllabusController {

    private final SyllabusService syllabusService;

    public SyllabusController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    @GetMapping
    @Operation(summary = "Get full aggregated syllabus tree", description = "Retrieves the complete aggregated hierarchy of exams, subjects, topics, and optional subjects")
    @ApiResponse(responseCode = "200", description = "Full syllabus tree retrieved successfully")
    public ResponseEntity<SyllabusTreeResponse> getFullSyllabusTree() {
        return ResponseEntity.ok(syllabusService.getFullSyllabusTree());
    }

    @GetMapping("/exams")
    @Operation(summary = "Get all exam stages", description = "Retrieves list of all exam stages (Prelims, Mains) ordered by display order")
    @ApiResponse(responseCode = "200", description = "List of exams retrieved successfully")
    public ResponseEntity<List<ExamResponse>> getAllExams() {
        return ResponseEntity.ok(syllabusService.getAllExams());
    }

    @GetMapping("/exams/{examId}")
    @Operation(summary = "Get exam details by ID", description = "Retrieves details of a specific exam stage")
    @ApiResponse(responseCode = "200", description = "Exam details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Exam not found")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable UUID examId) {
        return ResponseEntity.ok(syllabusService.getExamById(examId));
    }

    @GetMapping("/exams/{examId}/subjects")
    @Operation(summary = "Get subjects under an exam", description = "Retrieves all subjects/papers for a specific exam stage")
    @ApiResponse(responseCode = "200", description = "List of subjects retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Exam not found")
    public ResponseEntity<List<SubjectResponse>> getSubjectsByExamId(@PathVariable UUID examId) {
        return ResponseEntity.ok(syllabusService.getSubjectsByExamId(examId));
    }

    @GetMapping("/subjects/{subjectId}")
    @Operation(summary = "Get subject details by ID", description = "Retrieves details of a specific subject/paper")
    @ApiResponse(responseCode = "200", description = "Subject details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Subject not found")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(syllabusService.getSubjectById(subjectId));
    }

    @GetMapping("/subjects/{subjectId}/topics")
    @Operation(summary = "Get hierarchical topic tree for a subject", description = "Retrieves recursive topic and subtopic tree for a specific subject")
    @ApiResponse(responseCode = "200", description = "Topic tree retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Subject not found")
    public ResponseEntity<List<TopicResponse>> getTopicsBySubjectId(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(syllabusService.getTopicTreeBySubjectId(subjectId));
    }

    @GetMapping("/topics/{topicId}")
    @Operation(summary = "Get topic details by ID", description = "Retrieves details of a specific syllabus topic with its direct subtopics")
    @ApiResponse(responseCode = "200", description = "Topic details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Topic not found")
    public ResponseEntity<TopicResponse> getTopicById(@PathVariable UUID topicId) {
        return ResponseEntity.ok(syllabusService.getTopicById(topicId));
    }

    @GetMapping("/optionals")
    @Operation(summary = "Get optional subjects catalogue", description = "Retrieves the catalogue of UPSC optional subjects")
    @ApiResponse(responseCode = "200", description = "Optional subjects catalogue retrieved successfully")
    public ResponseEntity<List<OptionalSubjectResponse>> getAllOptionalSubjects() {
        return ResponseEntity.ok(syllabusService.getAllOptionalSubjects());
    }
}
