package com.aspirantos.controller;

import com.aspirantos.dto.progress.OverallProgressResponse;
import com.aspirantos.dto.progress.ProgressStatusRequest;
import com.aspirantos.dto.progress.SubjectProgressResponse;
import com.aspirantos.dto.progress.TopicProgressResponse;
import com.aspirantos.entity.ProgressStatus;
import com.aspirantos.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
@Tag(name = "Progress Tracking", description = "Endpoints for tracking user syllabus topic completion progress and PYQ tracker")
@SecurityRequirement(name = "Bearer Authentication")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    @Operation(summary = "Get overall preparation progress", description = "Retrieves overall, Prelims, and Mains syllabus completion metrics for authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Overall progress calculated successfully",
                    content = @Content(schema = @Schema(implementation = OverallProgressResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token")
    })
    public ResponseEntity<OverallProgressResponse> getOverallProgress() {
        return ResponseEntity.ok(progressService.getOverallProgress());
    }

    @GetMapping("/topics/{topicId}")
    @Operation(summary = "Get topic progress status", description = "Retrieves current completion status and PYQ status for a specific syllabus topic")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topic progress retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TopicProgressResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    public ResponseEntity<TopicProgressResponse> getTopicProgress(
            @Parameter(description = "Syllabus topic UUID", required = true)
            @PathVariable UUID topicId
    ) {
        return ResponseEntity.ok(progressService.getTopicProgress(topicId));
    }

    @PutMapping("/topics/{topicId}")
    @Operation(summary = "Update topic completion status & PYQ", description = "Updates status and/or PYQ done state of a topic for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topic status updated successfully",
                    content = @Content(schema = @Schema(implementation = TopicProgressResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    public ResponseEntity<TopicProgressResponse> updateTopicProgress(
            @Parameter(description = "Syllabus topic UUID", required = true)
            @PathVariable UUID topicId,
            @RequestBody ProgressStatusRequest request
    ) {
        if (request.getPyqDone() != null) {
            return ResponseEntity.ok(progressService.updateTopicProgress(topicId, request.getStatus(), request.getPyqDone()));
        }
        return ResponseEntity.ok(progressService.updateTopicProgress(topicId, request.getStatus()));
    }

    @PostMapping("/topics/{topicId}/pyq/toggle")
    @Operation(summary = "Toggle topic PYQ status", description = "Toggles PYQ solved state (true/false) for a syllabus topic")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PYQ status toggled successfully",
                    content = @Content(schema = @Schema(implementation = TopicProgressResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    public ResponseEntity<TopicProgressResponse> toggleTopicPyq(
            @Parameter(description = "Syllabus topic UUID", required = true)
            @PathVariable UUID topicId
    ) {
        return ResponseEntity.ok(progressService.togglePyqDone(topicId));
    }

    @GetMapping("/subjects/{subjectId}")
    @Operation(summary = "Get subject-level progress", description = "Retrieves total, completed, in-progress, and completion percentage for a subject/paper")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subject progress retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SubjectProgressResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<SubjectProgressResponse> getSubjectProgress(
            @Parameter(description = "Subject UUID", required = true)
            @PathVariable UUID subjectId
    ) {
        return ResponseEntity.ok(progressService.getSubjectProgress(subjectId));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all topic progress records as a map", description = "Retrieves all user progress records as a topicId -> status mapping for fast bulk hydration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All progress mappings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token")
    })
    public ResponseEntity<Map<UUID, ProgressStatus>> getAllTopicProgressMap() {
        return ResponseEntity.ok(progressService.getAllTopicProgressMap());
    }

    @GetMapping("/pyq-map")
    @Operation(summary = "Get all topic PYQ statuses as a map", description = "Retrieves all topicId -> pyqDone mappings for fast bulk hydration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All PYQ mappings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token")
    })
    public ResponseEntity<Map<UUID, Boolean>> getAllTopicPyqMap() {
        return ResponseEntity.ok(progressService.getAllTopicPyqMap());
    }
}
