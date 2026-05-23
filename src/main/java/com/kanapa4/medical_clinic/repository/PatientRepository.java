package com.kanapa4.medical_clinic.repository;

import com.kanapa4.medical_clinic.exception.PatientDoesNotExistsException;
import com.kanapa4.medical_clinic.exception.InvalidPatientException;
import com.kanapa4.medical_clinic.model.entity.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List<Patient> patients = new ArrayList<>();

    public Patient save(Patient patient) {
        if (patient == null) {
            throw new InvalidPatientException("Invalid Patient");
        }
        patients.add(patient);
        return patient;
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients);
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void deleteByEmail(String email) {
        patients.removeIf(patient -> patient.getEmail().equalsIgnoreCase(email));
    }

    public Patient update(Patient patient) {
        return findByEmail(patient.getEmail())
                .map(existingPatient -> {
                    existingPatient.update(patient);
                    return existingPatient;
                })
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exists"));
    }
}
