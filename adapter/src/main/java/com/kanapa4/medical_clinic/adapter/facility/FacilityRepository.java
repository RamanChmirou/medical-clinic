package com.kanapa4.medical_clinic.adapter.facility;

import com.kanapa4.medical_clinic.adapter.FacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<FacilityEntity, Long> {
    Optional<FacilityEntity> findByName(String name);
}
