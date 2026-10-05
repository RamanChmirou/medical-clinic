package com.kanapa4.medical_clinic.adapter.doctor;

import com.kanapa4.medical_clinic.adapter.DoctorEntity;
import com.kanapa4.medical_clinic.adapter.facility.FacilityRepository;
import com.kanapa4.medical_clinic.adapter.user.UserRepository;
import com.kanapa4.medical_clinic.domain.port.DoctorRepositoryPort;
import com.kanapa4.medical_clinic.model.Doctor;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Specialization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorRepositoryAdapter implements DoctorRepositoryPort {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public Page<Doctor> findAll(Pageable pageable) {
        org.springframework.data.domain.Pageable springPageable =
                PageRequest.of(pageable.getPage(), pageable.getSize());
        org.springframework.data.domain.Page<DoctorEntity> entityPage =
                doctorRepository.findAll(springPageable);

        return Page.<Doctor>builder()
                .content(entityPage.getContent().stream().map(doctorMapper::toPojo).toList())
                .page(entityPage.getNumber())
                .size(entityPage.getSize())
                .totalElements(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .build();
    }

    @Override
    public Page<Doctor> findBySpecialization(Specialization specialization, Pageable pageable) {
        org.springframework.data.domain.Pageable springPageable =
                PageRequest.of(pageable.getPage(), pageable.getSize());
        org.springframework.data.domain.Page<DoctorEntity> entityPage =
                doctorRepository.findBySpecialization(specialization, springPageable);

        return Page.<Doctor>builder()
                .content(entityPage.getContent().stream().map(doctorMapper::toPojo).toList())
                .page(entityPage.getNumber())
                .size(entityPage.getSize())
                .totalElements(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .build();
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
        DoctorEntity entity = doctorMapper.toEntity(doctor);
        entity.setUser(userRepository.getReferenceById(doctor.getUserId()));
        entity.setFacilities(new HashSet<>(facilityRepository.findAllById(doctor.getFacilityIds())));
        DoctorEntity saved = doctorRepository.save(entity);
        return doctorMapper.toPojo(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        doctorRepository.deleteById(id);
    }
}