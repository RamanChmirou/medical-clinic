package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Patient;

import java.util.Optional;

public interface PatientRepositoryPort {
    Page<Patient> findAll(Pageable pageable);

    Patient save(Patient patient);

    Optional<Patient> findById(Long id);

    Optional<Patient> findByIdCardNo(String idCardNo);

    void deleteById(Long id);
}
