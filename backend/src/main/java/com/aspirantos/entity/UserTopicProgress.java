package com.aspirantos.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "user_topic_progress",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_topic", columnNames = {"user_id", "topic_id"})
        },
        indexes = {
                @Index(name = "idx_progress_user", columnList = "user_id"),
                @Index(name = "idx_progress_topic", columnList = "topic_id"),
                @Index(name = "idx_progress_user_topic", columnList = "user_id, topic_id"),
                @Index(name = "idx_progress_status", columnList = "status"),
                @Index(name = "idx_progress_pyq", columnList = "pyq_done")
        }
)
public class UserTopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private SyllabusTopic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(name = "pyq_done")
    private Boolean pyqDone = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UserTopicProgress() {
    }

    public UserTopicProgress(UUID id, User user, SyllabusTopic topic, ProgressStatus status, Boolean pyqDone, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.topic = topic;
        this.status = status != null ? status : ProgressStatus.NOT_STARTED;
        this.pyqDone = pyqDone != null ? pyqDone : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private User user;
        private SyllabusTopic topic;
        private ProgressStatus status = ProgressStatus.NOT_STARTED;
        private Boolean pyqDone = false;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder topic(SyllabusTopic topic) {
            this.topic = topic;
            return this;
        }

        public Builder status(ProgressStatus status) {
            this.status = status;
            return this;
        }

        public Builder pyqDone(Boolean pyqDone) {
            this.pyqDone = pyqDone != null ? pyqDone : false;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserTopicProgress build() {
            return new UserTopicProgress(id, user, topic, status, pyqDone, createdAt, updatedAt);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SyllabusTopic getTopic() {
        return topic;
    }

    public void setTopic(SyllabusTopic topic) {
        this.topic = topic;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status != null ? status : ProgressStatus.NOT_STARTED;
    }

    public Boolean getPyqDone() {
        return pyqDone != null ? pyqDone : false;
    }

    public void setPyqDone(Boolean pyqDone) {
        this.pyqDone = pyqDone != null ? pyqDone : false;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTopicProgress that = (UserTopicProgress) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
