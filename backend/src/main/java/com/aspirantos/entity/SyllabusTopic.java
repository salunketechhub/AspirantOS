package com.aspirantos.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "syllabus_topics", indexes = {
        @Index(name = "idx_topics_subject", columnList = "subject_id"),
        @Index(name = "idx_topics_parent", columnList = "parent_topic_id"),
        @Index(name = "idx_topics_code", columnList = "code")
})
public class SyllabusTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_topic_id")
    private SyllabusTopic parentTopic;

    @OneToMany(mappedBy = "parentTopic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<SyllabusTopic> subtopics = new ArrayList<>();

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public SyllabusTopic() {}

    public SyllabusTopic(UUID id, Subject subject, SyllabusTopic parentTopic, List<SyllabusTopic> subtopics, String code, String name, String description, Integer level, Integer displayOrder, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.subject = subject;
        this.parentTopic = parentTopic;
        this.subtopics = subtopics != null ? subtopics : new ArrayList<>();
        this.code = code;
        this.name = name;
        this.description = description;
        this.level = level != null ? level : 1;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private Subject subject;
        private SyllabusTopic parentTopic;
        private List<SyllabusTopic> subtopics = new ArrayList<>();
        private String code;
        private String name;
        private String description;
        private Integer level = 1;
        private Integer displayOrder = 0;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder subject(Subject subject) { this.subject = subject; return this; }
        public Builder parentTopic(SyllabusTopic parentTopic) { this.parentTopic = parentTopic; return this; }
        public Builder subtopics(List<SyllabusTopic> subtopics) { this.subtopics = subtopics; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder level(Integer level) { this.level = level; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public SyllabusTopic build() {
            return new SyllabusTopic(id, subject, parentTopic, subtopics, code, name, description, level, displayOrder, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public SyllabusTopic getParentTopic() { return parentTopic; }
    public void setParentTopic(SyllabusTopic parentTopic) { this.parentTopic = parentTopic; }

    public List<SyllabusTopic> getSubtopics() { return subtopics; }
    public void setSubtopics(List<SyllabusTopic> subtopics) { this.subtopics = subtopics; }

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

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
