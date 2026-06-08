package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.dateTime >= :dayStart AND v.dateTime < :dayEnd")
    List<Visit> findAllByDoctorIdAndDate(
            @Param("doctorId") Long doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );

    List<Visit> findAllByPatientId(Long patientId);
}
