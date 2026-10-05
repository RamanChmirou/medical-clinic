package com.kanapa4.medical_clinic.adapter.visit;

import com.kanapa4.medical_clinic.adapter.VisitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<VisitEntity, Long>, JpaSpecificationExecutor<VisitEntity> {

    @Override
    @EntityGraph(attributePaths = {"patientEntity", "doctorEntity", "facility"})
    Page<VisitEntity> findAll(Specification<VisitEntity> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"patientEntity", "doctorEntity", "facility"})
    Page<VisitEntity> findAll(Pageable pageable);

    @Query("SELECT v FROM VisitEntity v WHERE v.doctorEntity.id = :doctorId AND v.dateTime >= :dayStart AND v.dateTime < :dayEnd")
    List<VisitEntity> findAllByDoctorIdAndDate(
            @Param("doctorId") Long doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );
}