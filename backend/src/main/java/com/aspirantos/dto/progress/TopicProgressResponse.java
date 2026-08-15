package com.aspirantos.dto.progress;

import com.aspirantos.entity.ProgressStatus;

import java.util.UUID;

public class TopicProgressResponse {

    private UUID topicId;
    private String topicCode;
    private String topicName;
    private ProgressStatus status;
    private UUID subjectId;
    private String subjectCode;
    private String subjectName;

    public TopicProgressResponse() {
    }

    public TopicProgressResponse(UUID topicId, String topicCode, String topicName, ProgressStatus status, UUID subjectId, String subjectCode, String subjectName) {
        this.topicId = topicId;
        this.topicCode = topicCode;
        this.topicName = topicName;
        this.status = status;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID topicId;
        private String topicCode;
        private String topicName;
        private ProgressStatus status;
        private UUID subjectId;
        private String subjectCode;
        private String subjectName;

        public Builder topicId(UUID topicId) {
            this.topicId = topicId;
            return this;
        }

        public Builder topicCode(String topicCode) {
            this.topicCode = topicCode;
            return this;
        }

        public Builder topicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public Builder status(ProgressStatus status) {
            this.status = status;
            return this;
        }

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

        public TopicProgressResponse build() {
            return new TopicProgressResponse(topicId, topicCode, topicName, status, subjectId, subjectCode, subjectName);
        }
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public String getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(String topicCode) {
        this.topicCode = topicCode;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
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
}
