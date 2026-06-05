package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    boolean existsByDoctorIdAndDateTime(Long doctorId, LocalDateTime dateTime);
    List<Visit> findAllByPatientId(Long patientId);
}
