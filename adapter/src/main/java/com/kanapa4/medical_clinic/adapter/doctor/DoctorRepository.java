package com.kanapa4.medical_clinic.adapter.doctor;

import com.kanapa4.medical_clinic.adapter.DoctorEntity;
import com.kanapa4.medical_clinic.model.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    Page<DoctorEntity> findBySpecialization(Specialization specialization, Pageable pageable);

    Optional<DoctorEntity> findByUserId(Long userId);
}