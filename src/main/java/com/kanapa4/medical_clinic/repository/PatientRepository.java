package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.entity.Patient;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Repository
public class PatientRepository {
    private final List<Patient> patientList = new ArrayList<>();

    public Patient save(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Invalid Patient");
        }
        patientList.add(patient);
        return patient;
    }

    public Optional<Patient> findByEmail(String email) {
        return patientList.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
