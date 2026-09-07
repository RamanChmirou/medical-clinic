package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.model.Specialization;
import com.kanapa4.medical_clinic.model.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long id);

    Page<Doctor> findBySpecialization(Specialization specialization, Pageable pageable);
}
