package com.kanapa4.medical_clinic.adapter.patient;

import com.kanapa4.medical_clinic.adapter.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByIdCardNo(String idCardNo);

    void deleteById(@NonNull Long id);
}


