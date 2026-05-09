package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.entity.Patient;
import com.kanapa4.medical_clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.getPatientList();
    }

    public Patient findByEmail(String email){
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient does not exists"));
    }
}
