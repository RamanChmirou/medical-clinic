package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Facility;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;

import java.util.Optional;

public interface FacilityRepositoryPort {
    Page<Facility> findAll(Pageable pageable);

    Optional<Facility> findById(Long id);

    Optional<Facility> findByName(String name);

    Facility save(Facility facility);

    void deleteById(Long id);
}
