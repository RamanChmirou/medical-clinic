package com.kanapa4.medical_clinic.adapter.patient;

import com.kanapa4.medical_clinic.adapter.PageMapper;
import com.kanapa4.medical_clinic.adapter.PatientEntity;
import com.kanapa4.medical_clinic.domain.port.PatientRepositoryPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Patient;
import com.kanapa4.medical_clinic.model.exception.PatientAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientRepositoryAdapter implements PatientRepositoryPort {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public Page<Patient> findAll(Pageable pageable) {
        var springPage = patientRepository.findAll(PageMapper.toSpringPageable(pageable));
        return PageMapper.toPojoPage(springPage, patientMapper::toPojo);
    }

    @Override
    @Transactional
    public Patient save(Patient patient) {
        if (patientRepository.findByIdCardNo(patient.getIdCardNo()).isPresent()) {
            throw new PatientAlreadyExistsException("Patient with this id card number already exists");
        }
        PatientEntity entity = patientMapper.toEntity(patient);
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