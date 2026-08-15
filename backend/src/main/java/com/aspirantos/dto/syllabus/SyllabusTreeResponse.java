package com.aspirantos.dto.syllabus;

import com.aspirantos.entity.ExamStage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyllabusTreeResponse {

    private List<ExamTreeResponse> exams = new ArrayList<>();
    private List<OptionalSubjectResponse> optionals = new ArrayList<>();
    private int totalExams;
    private int totalSubjects;
    private int totalTopics;
    private int totalOptionals;

    public SyllabusTreeResponse() {}

    public SyllabusTreeResponse(List<ExamTreeResponse> exams, List<OptionalSubjectResponse> optionals, int totalExams, int totalSubjects, int totalTopics, int totalOptionals) {
        this.exams = exams != null ? exams : new ArrayList<>();
        this.optionals = optionals != null ? optionals : new ArrayList<>();
        this.totalExams = totalExams;
        this.totalSubjects = totalSubjects;
        this.totalTopics = totalTopics;
        this.totalOptionals = totalOptionals;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<ExamTreeResponse> exams = new ArrayList<>();
        private List<OptionalSubjectResponse> optionals = new ArrayList<>();
        private int totalExams;
        private int totalSubjects;
        private int totalTopics;
        private int totalOptionals;

        public Builder exams(List<ExamTreeResponse> exams) { this.exams = exams; return this; }
        public Builder optionals(List<OptionalSubjectResponse> optionals) { this.optionals = optionals; return this; }
        public Builder totalExams(int totalExams) { this.totalExams = totalExams; return this; }
        public Builder totalSubjects(int totalSubjects) { this.totalSubjects = totalSubjects; return this; }
        public Builder totalTopics(int totalTopics) { this.totalTopics = totalTopics; return this; }
        public Builder totalOptionals(int totalOptionals) { this.totalOptionals = totalOptionals; return this; }

        public SyllabusTreeResponse build() {
            return new SyllabusTreeResponse(exams, optionals, totalExams, totalSubjects, totalTopics, totalOptionals);
        }
    }

    public List<ExamTreeResponse> getExams() { return exams; }
    public void setExams(List<ExamTreeResponse> exams) { this.exams = exams; }

    public List<OptionalSubjectResponse> getOptionals() { return optionals; }
    public void setOptionals(List<OptionalSubjectResponse> optionals) { this.optionals = optionals; }

    public int getTotalExams() { return totalExams; }
    public void setTotalExams(int totalExams) { this.totalExams = totalExams; }

    public int getTotalSubjects() { return totalSubjects; }
    public void setTotalSubjects(int totalSubjects) { this.totalSubjects = totalSubjects; }

    public int getTotalTopics() { return totalTopics; }
    public void setTotalTopics(int totalTopics) { this.totalTopics = totalTopics; }

    public int getTotalOptionals() { return totalOptionals; }
    public void setTotalOptionals(int totalOptionals) { this.totalOptionals = totalOptionals; }

    public static class ExamTreeResponse {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private ExamStage stage;
        private Integer displayOrder;
        private List<SubjectTreeResponse> subjects = new ArrayList<>();

        public ExamTreeResponse() {}

        public ExamTreeResponse(UUID id, String code, String name, String description, ExamStage stage, Integer displayOrder, List<SubjectTreeResponse> subjects) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.description = description;
            this.stage = stage;
            this.displayOrder = displayOrder;
            this.subjects = subjects != null ? subjects : new ArrayList<>();
        }

        public static ExamTreeResponseBuilder builder() {
            return new ExamTreeResponseBuilder();
        }

        public static class ExamTreeResponseBuilder {
            private UUID id;
            private String code;
            private String name;
            private String description;
            private ExamStage stage;
            private Integer displayOrder;
            private List<SubjectTreeResponse> subjects = new ArrayList<>();

            public ExamTreeResponseBuilder id(UUID id) { this.id = id; return this; }
            public ExamTreeResponseBuilder code(String code) { this.code = code; return this; }
            public ExamTreeResponseBuilder name(String name) { this.name = name; return this; }
            public ExamTreeResponseBuilder description(String description) { this.description = description; return this; }
            public ExamTreeResponseBuilder stage(ExamStage stage) { this.stage = stage; return this; }
            public ExamTreeResponseBuilder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
            public ExamTreeResponseBuilder subjects(List<SubjectTreeResponse> subjects) { this.subjects = subjects; return this; }

            public ExamTreeResponse build() {
                return new ExamTreeResponse(id, code, name, description, stage, displayOrder, subjects);
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

        public List<SubjectTreeResponse> getSubjects() { return subjects; }
        public void setSubjects(List<SubjectTreeResponse> subjects) { this.subjects = subjects; }
    }

    public static class SubjectTreeResponse {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private String paper;
        private Integer displayOrder;
        private List<TopicResponse> topics = new ArrayList<>();

        public SubjectTreeResponse() {}

        public SubjectTreeResponse(UUID id, String code, String name, String description, String paper, Integer displayOrder, List<TopicResponse> topics) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.description = description;
            this.paper = paper;
            this.displayOrder = displayOrder;
            this.topics = topics != null ? topics : new ArrayList<>();
        }

        public static SubjectTreeResponseBuilder builder() {
            return new SubjectTreeResponseBuilder();
        }

        public static class SubjectTreeResponseBuilder {
            private UUID id;
            private String code;
            private String name;
            private String description;
            private String paper;
            private Integer displayOrder;
            private List<TopicResponse> topics = new ArrayList<>();

            public SubjectTreeResponseBuilder id(UUID id) { this.id = id; return this; }
            public SubjectTreeResponseBuilder code(String code) { this.code = code; return this; }
            public SubjectTreeResponseBuilder name(String name) { this.name = name; return this; }
            public SubjectTreeResponseBuilder description(String description) { this.description = description; return this; }
            public SubjectTreeResponseBuilder paper(String paper) { this.paper = paper; return this; }
            public SubjectTreeResponseBuilder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
            public SubjectTreeResponseBuilder topics(List<TopicResponse> topics) { this.topics = topics; return this; }

            public SubjectTreeResponse build() {
                return new SubjectTreeResponse(id, code, name, description, paper, displayOrder, topics);
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

        public String getPaper() { return paper; }
        public void setPaper(String paper) { this.paper = paper; }

        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

        public List<TopicResponse> getTopics() { return topics; }
        public void setTopics(List<TopicResponse> topics) { this.topics = topics; }
    }
}
