package com.kanapa4.medical_clinic.adapter.doctor;

import com.kanapa4.medical_clinic.adapter.DoctorEntity;
import com.kanapa4.medical_clinic.adapter.FacilityEntity;
import com.kanapa4.medical_clinic.adapter.PageMapper;
import com.kanapa4.medical_clinic.adapter.facility.FacilityRepository;
import com.kanapa4.medical_clinic.adapter.user.UserRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

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
        if (doctor.getUserId() != null) {
            doctorRepository.findByUserId(doctor.getUserId())
                    .filter(existing -> !existing.getId().equals(doctor.getId()))
                    .ifPresent(existing -> {
                        throw new DoctorAlreadyExistsException("Doctor with this user id already exists");
                    });
        }

        DoctorEntity entity = doctorMapper.toEntity(doctor);
        bindRelations(entity, doctor);

        DoctorEntity savedEntity = doctorRepository.save(entity);
        return doctorMapper.toPojo(savedEntity);
    }

    private void bindRelations(DoctorEntity entity, Doctor doctor) {
        Optional.ofNullable(doctor.getUserId())
                .map(userRepository::getReferenceById)
                .ifPresent(entity::setUser);

        if (doctor.getFacilityIds() != null) {
            Set<FacilityEntity> facilities = doctor.getFacilityIds().stream()
                    .map(facilityRepository::getReferenceById)
                    .collect(Collectors.toSet());
            entity.setFacilities(facilities);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        doctorRepository.deleteById(id);
    }
}