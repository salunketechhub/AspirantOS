package com.aspirantos.repository;

import com.aspirantos.entity.SyllabusTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SyllabusTopicRepository extends JpaRepository<SyllabusTopic, UUID> {

    List<SyllabusTopic> findBySubjectIdOrderByDisplayOrderAsc(UUID subjectId);

    List<SyllabusTopic> findBySubjectIdAndParentTopicIsNullOrderByDisplayOrderAsc(UUID subjectId);

    List<SyllabusTopic> findByParentTopicIdOrderByDisplayOrderAsc(UUID parentTopicId);

    Optional<SyllabusTopic> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    long countBySubjectId(UUID subjectId);

    long countBySubject_Exam_Stage(com.aspirantos.entity.ExamStage stage);
}
