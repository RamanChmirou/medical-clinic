package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Doctor;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Specialization;

import java.util.Optional;

public interface DoctorRepositoryPort {
    Page<Doctor> findAll(Pageable pageable);

    Page<Doctor> findBySpecialization(Specialization specialization, Pageable pageable);

    Optional<Doctor> findById(Long id);

    Optional<Doctor> findByUserId(Long userId);

    Doctor save(Doctor doctor);

    void deleteById(Long id);
}