package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserEmail(String email);
    void deleteByUserEmail(String email);
}
