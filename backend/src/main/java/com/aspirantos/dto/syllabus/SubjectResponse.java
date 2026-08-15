package com.aspirantos.dto.syllabus;

import java.util.UUID;

public class SubjectResponse {
    private UUID id;
    private UUID examId;
    private String examCode;
    private String examName;
    private String code;
    private String name;
    private String description;
    private String paper;
    private Integer displayOrder;
    private int topicCount;

    public SubjectResponse() {}

    public SubjectResponse(UUID id, UUID examId, String examCode, String examName, String code, String name, String description, String paper, Integer displayOrder, int topicCount) {
        this.id = id;
        this.examId = examId;
        this.examCode = examCode;
        this.examName = examName;
        this.code = code;
        this.name = name;
        this.description = description;
        this.paper = paper;
        this.displayOrder = displayOrder;
        this.topicCount = topicCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID examId;
        private String examCode;
        private String examName;
        private String code;
        private String name;
        private String description;
        private String paper;
        private Integer displayOrder;
        private int topicCount;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder examId(UUID examId) { this.examId = examId; return this; }
        public Builder examCode(String examCode) { this.examCode = examCode; return this; }
        public Builder examName(String examName) { this.examName = examName; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder paper(String paper) { this.paper = paper; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder topicCount(int topicCount) { this.topicCount = topicCount; return this; }

        public SubjectResponse build() {
            return new SubjectResponse(id, examId, examCode, examName, code, name, description, paper, displayOrder, topicCount);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getExamId() { return examId; }
    public void setExamId(UUID examId) { this.examId = examId; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPaper() { return paper; }
    public void setPaper(String paper) { this.paper = paper; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public int getTopicCount() { return topicCount; }
    public void setTopicCount(int topicCount) { this.topicCount = topicCount; }
}
