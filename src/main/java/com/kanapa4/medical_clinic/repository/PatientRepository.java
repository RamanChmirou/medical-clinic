package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByIdCardNo(String idCardNo);
    List<Patient> findAllByUserEmail(String email);
    void deleteByIdCardNo(String idCardNo);

    String id(Long id);
}
