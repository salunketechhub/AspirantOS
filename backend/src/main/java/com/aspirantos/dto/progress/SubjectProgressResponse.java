package com.aspirantos.dto.progress;

import java.util.UUID;

public class SubjectProgressResponse {

    private UUID subjectId;
    private String subjectCode;
    private String subjectName;
    private int totalTopics;
    private int completedTopics;
    private int inProgressTopics;
    private int notStartedTopics;
    private int completionPercentage;

    public SubjectProgressResponse() {
    }

    public SubjectProgressResponse(UUID subjectId, String subjectCode, String subjectName, int totalTopics, int completedTopics, int inProgressTopics, int notStartedTopics, int completionPercentage) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.totalTopics = totalTopics;
        this.completedTopics = completedTopics;
        this.inProgressTopics = inProgressTopics;
        this.notStartedTopics = notStartedTopics;
        this.completionPercentage = completionPercentage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID subjectId;
        private String subjectCode;
        private String subjectName;
        private int totalTopics;
        private int completedTopics;
        private int inProgressTopics;
        private int notStartedTopics;
        private int completionPercentage;

        public Builder subjectId(UUID subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public Builder subjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public Builder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public Builder totalTopics(int totalTopics) {
            this.totalTopics = totalTopics;
            return this;
        }

        public Builder completedTopics(int completedTopics) {
            this.completedTopics = completedTopics;
            return this;
        }

        public Builder inProgressTopics(int inProgressTopics) {
            this.inProgressTopics = inProgressTopics;
            return this;
        }

        public Builder notStartedTopics(int notStartedTopics) {
            this.notStartedTopics = notStartedTopics;
            return this;
        }

        public Builder completionPercentage(int completionPercentage) {
            this.completionPercentage = completionPercentage;
            return this;
        }

        public SubjectProgressResponse build() {
            return new SubjectProgressResponse(subjectId, subjectCode, subjectName, totalTopics, completedTopics, inProgressTopics, notStartedTopics, completionPercentage);
        }
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getTotalTopics() {
        return totalTopics;
    }

    public void setTotalTopics(int totalTopics) {
        this.totalTopics = totalTopics;
    }

    public int getCompletedTopics() {
        return completedTopics;
    }

    public void setCompletedTopics(int completedTopics) {
        this.completedTopics = completedTopics;
    }

    public int getInProgressTopics() {
        return inProgressTopics;
    }

    public void setInProgressTopics(int inProgressTopics) {
        this.inProgressTopics = inProgressTopics;
    }

    public int getNotStartedTopics() {
        return notStartedTopics;
    }

    public void setNotStartedTopics(int notStartedTopics) {
        this.notStartedTopics = notStartedTopics;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
