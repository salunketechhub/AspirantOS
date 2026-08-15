package com.aspirantos.repository;

import com.aspirantos.entity.Exam;
import com.aspirantos.entity.ExamStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {

    List<Exam> findAllByOrderByDisplayOrderAsc();

    List<Exam> findByStageOrderByDisplayOrderAsc(ExamStage stage);

    Optional<Exam> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}
