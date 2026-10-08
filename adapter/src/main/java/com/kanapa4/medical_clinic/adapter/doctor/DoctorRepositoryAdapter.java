package com.kanapa4.medical_clinic.adapter.doctor;

import com.kanapa4.medical_clinic.adapter.DoctorEntity;
import com.kanapa4.medical_clinic.adapter.PageMapper;
import com.kanapa4.medical_clinic.domain.port.DoctorRepositoryPort;
import com.kanapa4.medical_clinic.model.Doctor;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Specialization;
import com.kanapa4.medical_clinic.model.exception.DoctorAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorRepositoryAdapter implements DoctorRepositoryPort {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public Page<Doctor> findAll(Pageable pageable) {
        var springPage = doctorRepository.findAll(PageMapper.toSpringPageable(pageable));
        return PageMapper.toPojoPage(springPage, doctorMapper::toPojo);
    }

    @Override
    public Page<Doctor> findBySpecialization(Specialization specialization, Pageable pageable) {
        var springPage = doctorRepository.findBySpecialization(specialization, PageMapper.toSpringPageable(pageable));
        return PageMapper.toPojoPage(springPage, doctorMapper::toPojo);
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id).map(doctorMapper::toPojo);
    }

    @Override
    public Optional<Doctor> findByUserId(Long userId) {
        return doctorRepository.findByUserId(userId).map(doctorMapper::toPojo);
    }

    @Override
    @Transactional
    public Doctor save(Doctor doctor) {
        if (doctorRepository.findByUserId(doctor.getUserId()).isPresent()) {
            throw new DoctorAlreadyExistsException("Doctor with this user id already exists");
        }
        DoctorEntity entity = doctorMapper.toEntity(doctor);
        DoctorEntity savedEntity = doctorRepository.save(entity);
        return doctorMapper.toPojo(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        doctorRepository.deleteById(id);
    }
}