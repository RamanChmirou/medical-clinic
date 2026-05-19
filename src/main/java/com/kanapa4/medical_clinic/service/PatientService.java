package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.model.Patient;
import com.kanapa4.medical_clinic.exception.PatientAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.PatientDoesNotExistsException;
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
        return patientRepository.findAll();
    }

    public Patient findByEmail(String email){
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exists"));
    }

    public Patient create(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientAlreadyExistsException("Patient already exists");
        }
        return patientRepository.save(patient);
    }

    public Patient update(String email, Patient patientData) {
        Patient existing = findByEmail(email);
        existing.update(patientData);
        return patientRepository.update(existing);
    }

    public void delete(String email) {
        patientRepository.deleteByEmail(email);
    }

    public void editPassword(String email, String password) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exists"));
        patient.setPassword(password);
    }
}
