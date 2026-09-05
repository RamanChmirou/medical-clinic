package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.PatientAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.PatientDoesNotExistsException;
import com.kanapa4.medical_clinic.exception.UserDoesNotExistsException;
import com.kanapa4.medical_clinic.mapper.PatientMapper;
import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.model.entity.Patient;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.PatientRepository;
import com.kanapa4.medical_clinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;

    public Page<PatientDto> getPaginatedPatients(int page, int size, String sortBy) {
        if (size > 30) {
            size = 30;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return patientRepository.findAll(pageable).map(patientMapper::toDto);
    }

    @Transactional
    public PatientDto createPatientForUser(String userEmail, PatientCreateCommand command) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserDoesNotExistsException("User not found"));
        Patient newPatient = Patient.create(command, user);
        Patient savedPatient = patientRepository.save(newPatient);
        return patientMapper.toDto(savedPatient);
    }

    public PatientDto findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));
        return patientMapper.toDto(patient);
    }

    public PatientDto create(PatientCreateCommand dto) {
        if (patientRepository.findByIdCardNo(dto.getIdCardNo()).isPresent()) {
            throw new PatientAlreadyExistsException("Patient already exists");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));
        Patient patient = Patient.create(dto, user);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }

    @Transactional
    public PatientDto update(Long id, PatientDto dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhoneNumber(dto.getPhoneNumber());
        Patient savedPatient = patientRepository.save(existing);
        return patientMapper.toDto(savedPatient);
    }

    @Transactional
    public void delete(Long id) {
        if (patientRepository.findById(id).isEmpty()) {
            throw new PatientDoesNotExistsException("Patient does not exist");
        }
        patientRepository.deleteById(id);
    }
}
