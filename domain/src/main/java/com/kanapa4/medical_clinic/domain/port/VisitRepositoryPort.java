package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Visit;
import com.kanapa4.medical_clinic.model.VisitFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepositoryPort {
    Page<Visit> findAll(Pageable pageable);

    Page<Visit> findAll(VisitFilter filter, Pageable pageable);

    Optional<Visit> findById(Long id);

    List<Visit> findAllByDoctorIdAndDate(Long doctorId, LocalDateTime dayStart, LocalDateTime dayEnd);

    boolean existsById(Long id);

    Visit save(Visit visit);

    void deleteById(Long id);
}