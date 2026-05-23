package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.PatientAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.PatientDoesNotExistsException;
import com.kanapa4.medical_clinic.mapper.PatientMapper;
import com.kanapa4.medical_clinic.model.dto.PatientCreatedDto;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.model.entity.Patient;
import com.kanapa4.medical_clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .toList();
    }

    public PatientDto findByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));
        return patientMapper.toDto(patient);
    }

    public PatientDto create(PatientCreatedDto dto) {
        if (patientRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new PatientAlreadyExistsException("Patient already exists");
        }

        Patient patient = Patient.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }

    public PatientDto update(String email, PatientDto dto) {
        Patient existing = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());

        Patient updated = patientRepository.update(existing);
        return patientMapper.toDto(updated);
    }

    public void delete(String email) {
        if (patientRepository.findByEmail(email).isEmpty()) {
            throw new PatientDoesNotExistsException("Patient does not exist");
        }
        patientRepository.deleteByEmail(email);
    }

    public void editPassword(String email, String password) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));
        patient.setPassword(password);
        patientRepository.update(patient);
    }
}
