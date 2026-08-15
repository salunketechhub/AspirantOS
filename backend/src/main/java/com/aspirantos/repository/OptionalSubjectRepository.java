package com.aspirantos.repository;

import com.aspirantos.entity.OptionalSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OptionalSubjectRepository extends JpaRepository<OptionalSubject, UUID> {

    List<OptionalSubject> findAllByOrderByDisplayOrderAsc();

    Optional<OptionalSubject> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}
