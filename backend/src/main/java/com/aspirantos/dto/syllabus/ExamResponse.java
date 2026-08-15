package com.aspirantos.dto.syllabus;

import com.aspirantos.entity.ExamStage;

import java.util.UUID;

public class ExamResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private ExamStage stage;
    private Integer displayOrder;
    private int subjectCount;

    public ExamResponse() {}

    public ExamResponse(UUID id, String code, String name, String description, ExamStage stage, Integer displayOrder, int subjectCount) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.stage = stage;
        this.displayOrder = displayOrder;
        this.subjectCount = subjectCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private ExamStage stage;
        private Integer displayOrder;
        private int subjectCount;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder stage(ExamStage stage) { this.stage = stage; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder subjectCount(int subjectCount) { this.subjectCount = subjectCount; return this; }

        public ExamResponse build() {
            return new ExamResponse(id, code, name, description, stage, displayOrder, subjectCount);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ExamStage getStage() { return stage; }
    public void setStage(ExamStage stage) { this.stage = stage; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public int getSubjectCount() { return subjectCount; }
    public void setSubjectCount(int subjectCount) { this.subjectCount = subjectCount; }
}
