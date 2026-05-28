package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.PatientAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.PatientDoesNotExistsException;
import com.kanapa4.medical_clinic.mapper.PatientMapper;
import com.kanapa4.medical_clinic.model.Role;
import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.model.entity.Patient;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.PatientRepository;
import jakarta.transaction.Transactional;
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
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));
        return patientMapper.toDto(patient);
    }

    public PatientDto create(PatientCreateCommand dto) {
        if (patientRepository.findByUserEmail(dto.getEmail()).isPresent()) {
            throw new PatientAlreadyExistsException("Patient already exists");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.PATIENT)
                .build();

        Patient patient = Patient.builder()
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .birthday(dto.getBirthday())
                .idCardNo(dto.getIdCardNo())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }

    @Transactional
    public PatientDto update(String email, PatientDto dto) {
        Patient existing = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhoneNumber(dto.getPhoneNumber());

        return patientMapper.toDto(existing);
    }

    @Transactional
    public void delete(String email) {
        if (patientRepository.findByUserEmail(email).isEmpty()) {
            throw new PatientDoesNotExistsException("Patient does not exist");
        }
        patientRepository.deleteByUserEmail(email);
    }

    @Transactional
    public void editPassword(String email, String password) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));
        patient.getUser().setPassword(password);
    }
}
