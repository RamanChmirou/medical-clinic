package com.kanapa4.medical_clinic.domain;

import com.kanapa4.medical_clinic.domain.port.PatientMapperPort;
import com.kanapa4.medical_clinic.domain.port.PatientRepositoryPort;
import com.kanapa4.medical_clinic.domain.port.UserRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.PatientAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.PatientDoesNotExistsException;
import com.kanapa4.medical_clinic.model.exception.UserDoesNotExistsException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PatientService {
    private final PatientRepositoryPort patientRepositoryPort;
    private final PatientMapperPort patientMapperPort;
    private final UserRepositoryPort userRepositoryPort;

    public Page<PatientDto> getPaginatedPatients(Pageable pageable) {
        return patientMapperPort.toPageDto(patientRepositoryPort.findAll(pageable));
    }

    public PatientDto createPatientForUser(String userEmail, PatientCreateCommand command) {
        User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new UserDoesNotExistsException("User not found"));
        Patient newPatient = Patient.create(command, user.getId());
        Patient savedPatient = patientRepositoryPort.save(newPatient);
        return patientMapperPort.toDto(savedPatient);
    }

    public PatientDto findById(Long id) {
        Patient patient = patientRepositoryPort.findById(id)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));
        return patientMapperPort.toDto(patient);
    }

    public PatientDto create(PatientCreateCommand dto) {
        if (patientRepositoryPort.findByIdCardNo(dto.getIdCardNo()).isPresent()) {
            throw new PatientAlreadyExistsException("Patient already exists");
        }
        User user = userRepositoryPort.findById(dto.getUserId())
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));
        Patient patient = Patient.create(dto, user.getId());
        Patient savedPatient = patientRepositoryPort.save(patient);
        return patientMapperPort.toDto(savedPatient);
    }

    public PatientDto update(Long id, PatientDto dto) {
        Patient existing = patientRepositoryPort.findById(id)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhoneNumber(dto.getPhoneNumber());
        Patient savedPatient = patientRepositoryPort.save(existing);
        return patientMapperPort.toDto(savedPatient);
    }

    public void delete(Long id) {
        if (patientRepositoryPort.findById(id).isEmpty()) {
            throw new PatientDoesNotExistsException("Patient does not exist");
        }
        patientRepositoryPort.deleteById(id);
    }
}