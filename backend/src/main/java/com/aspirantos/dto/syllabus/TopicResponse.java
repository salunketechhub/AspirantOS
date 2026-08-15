package com.aspirantos.dto.syllabus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TopicResponse {
    private UUID id;
    private UUID subjectId;
    private String subjectCode;
    private String subjectName;
    private UUID parentTopicId;
    private String code;
    private String name;
    private String description;
    private Integer level;
    private Integer displayOrder;
    private List<TopicResponse> subtopics = new ArrayList<>();
    private int subtopicCount;

    public TopicResponse() {}

    public TopicResponse(UUID id, UUID subjectId, String subjectCode, String subjectName, UUID parentTopicId, String code, String name, String description, Integer level, Integer displayOrder, List<TopicResponse> subtopics, int subtopicCount) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.parentTopicId = parentTopicId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.level = level;
        this.displayOrder = displayOrder;
        this.subtopics = subtopics != null ? subtopics : new ArrayList<>();
        this.subtopicCount = subtopicCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID subjectId;
        private String subjectCode;
        private String subjectName;
        private UUID parentTopicId;
        private String code;
        private String name;
        private String description;
        private Integer level;
        private Integer displayOrder;
        private List<TopicResponse> subtopics = new ArrayList<>();
        private int subtopicCount;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder subjectId(UUID subjectId) { this.subjectId = subjectId; return this; }
        public Builder subjectCode(String subjectCode) { this.subjectCode = subjectCode; return this; }
        public Builder subjectName(String subjectName) { this.subjectName = subjectName; return this; }
        public Builder parentTopicId(UUID parentTopicId) { this.parentTopicId = parentTopicId; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder level(Integer level) { this.level = level; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder subtopics(List<TopicResponse> subtopics) { this.subtopics = subtopics; return this; }
        public Builder subtopicCount(int subtopicCount) { this.subtopicCount = subtopicCount; return this; }

        public TopicResponse build() {
            return new TopicResponse(id, subjectId, subjectCode, subjectName, parentTopicId, code, name, description, level, displayOrder, subtopics, subtopicCount);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSubjectId() { return subjectId; }
    public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public UUID getParentTopicId() { return parentTopicId; }
    public void setParentTopicId(UUID parentTopicId) { this.parentTopicId = parentTopicId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public List<TopicResponse> getSubtopics() { return subtopics; }
    public void setSubtopics(List<TopicResponse> subtopics) { this.subtopics = subtopics; }

    public int getSubtopicCount() { return subtopicCount; }
    public void setSubtopicCount(int subtopicCount) { this.subtopicCount = subtopicCount; }
}
