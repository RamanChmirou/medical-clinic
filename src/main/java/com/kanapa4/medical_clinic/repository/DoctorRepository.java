package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long id);
}
