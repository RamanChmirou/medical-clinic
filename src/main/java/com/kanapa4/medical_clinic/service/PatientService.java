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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;

    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .toList();
    }

    public List<PatientDto> findAllByUserEmail(String email) {
        return patientRepository.findAllByUserEmail(email).stream()
                .map(patientMapper::toDto)
                .toList();
    }

    public PatientDto findByIdCardNo(String idCardNo) {
        Patient patient = patientRepository.findByIdCardNo(idCardNo)
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
    public PatientDto update(String idCardNo, PatientDto dto) {
        Patient existing = patientRepository.findByIdCardNo(idCardNo)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhoneNumber(dto.getPhoneNumber());

        return patientMapper.toDto(existing);
    }

    @Transactional
    public void delete(String idCardNo) {
        if (patientRepository.findByIdCardNo(idCardNo).isEmpty()) {
            throw new PatientDoesNotExistsException("Patient does not exist");
        }
        patientRepository.deleteByIdCardNo(idCardNo);
    }
}
