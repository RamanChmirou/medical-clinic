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

    public Patient create(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new RuntimeException("Patient already exists");
        }
        return patientRepository.save(patient);
    }

    public Patient update(String email, Patient patient) {
        Patient existing = findByEmail(email);

        existing.setFirstName(patient.getFirstName());
        existing.setLastName(patient.getLastName());
        existing.setPhoneNumber(patient.getPhoneNumber());
        existing.setIdCardNo(patient.getIdCardNo());
        existing.setBirthday(patient.getBirthday());
        existing.setPassword(patient.getPassword());

        return existing;
    }

    public void delete(String email) {
        patientRepository.getPatientList()
                .removeIf(p -> p.getEmail().equalsIgnoreCase(email));
    }
}
