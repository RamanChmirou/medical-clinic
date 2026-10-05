package com.kanapa4.medical_clinic.adapter.patient;

import com.kanapa4.medical_clinic.adapter.PatientEntity;
import com.kanapa4.medical_clinic.adapter.user.UserRepository;
import com.kanapa4.medical_clinic.domain.port.PatientRepositoryPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientRepositoryAdapter implements PatientRepositoryPort {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;

    @Override
    public Page<Patient> findAll(Pageable pageable) {
        var springPageable = PageRequest.of(pageable.getPage(), pageable.getSize());
        var springPage = patientRepository.findAll(springPageable);
        List<Patient> domainPatients = springPage.getContent().stream()
                .map(patientMapper::toPojo)
                .toList();

        return Page.<Patient>builder()
                .content(domainPatients)
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public Patient save(Patient patient) {
        PatientEntity entity = patientMapper.toEntity(patient);
        entity.setUser(userRepository.getReferenceById(patient.getUserId()));
        PatientEntity savedEntity = patientRepository.save(entity);
        return patientMapper.toPojo(savedEntity);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id)
                .map(patientMapper::toPojo);
    }

    @Override
    public Optional<Patient> findByIdCardNo(String idCardNo) {
        return patientRepository.findByIdCardNo(idCardNo)
                .map(patientMapper::toPojo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }
}