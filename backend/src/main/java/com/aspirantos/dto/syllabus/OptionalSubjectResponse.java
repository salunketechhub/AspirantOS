package com.aspirantos.dto.syllabus;

import java.util.UUID;

public class OptionalSubjectResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Integer displayOrder;

    public OptionalSubjectResponse() {}

    public OptionalSubjectResponse(UUID id, String code, String name, String description, Integer displayOrder) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private Integer displayOrder;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }

        public OptionalSubjectResponse build() {
            return new OptionalSubjectResponse(id, code, name, description, displayOrder);
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

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
