package com.kanapa4.medical_clinic.domain;

import com.kanapa4.medical_clinic.domain.port.DoctorMapperPort;
import com.kanapa4.medical_clinic.domain.port.DoctorRepositoryPort;
import com.kanapa4.medical_clinic.domain.port.FacilityRepositoryPort;
import com.kanapa4.medical_clinic.domain.port.UserRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.DoctorAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.DoctorDoesNotExistsException;
import com.kanapa4.medical_clinic.model.exception.FacilityDoesNotExistsException;
import com.kanapa4.medical_clinic.model.exception.UserDoesNotExistsException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepositoryPort doctorRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final FacilityRepositoryPort facilityRepositoryPort;
    private final DoctorMapperPort doctorMapperPort;

    public Page<DoctorDto> getPaginatedDoctors(Pageable pageable, Specialization specialization) {
        if (specialization != null) {
            return doctorMapperPort.toPageDto(doctorRepositoryPort.findBySpecialization(specialization, pageable));
        }
        return doctorMapperPort.toPageDto(doctorRepositoryPort.findAll(pageable));
    }

    public DoctorDto findById(Long id) {
        Doctor doctor = doctorRepositoryPort.findById(id)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));
        return doctorMapperPort.toDto(doctor);
    }

    public DoctorDto create(DoctorCreateCommand command) {
        userRepositoryPort.findById(command.getUserId())
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));

        if (doctorRepositoryPort.findByUserId(command.getUserId()).isPresent()) {
            throw new DoctorAlreadyExistsException("This user is already a doctor");
        }

        Doctor doctor = Doctor.create(command);
        Doctor savedDoctor = doctorRepositoryPort.save(doctor);
        return doctorMapperPort.toDto(savedDoctor);
    }

    public DoctorDto update(Long id, DoctorDto dto) {
        Doctor existing = doctorRepositoryPort.findById(id)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setSpecialization(dto.getSpecialization());
        Doctor updatedDoctor = doctorRepositoryPort.save(existing);
        return doctorMapperPort.toDto(updatedDoctor);
    }

    public void delete(Long id) {
        if (doctorRepositoryPort.findById(id).isEmpty()) {
            throw new DoctorDoesNotExistsException("Doctor does not exist");
        }
        doctorRepositoryPort.deleteById(id);
    }

    public DoctorDto addFacilityToDoctor(Long doctorId, Long facilityId) {
        Doctor doctor = doctorRepositoryPort.findById(doctorId)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));
        facilityRepositoryPort.findById(facilityId)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));

        doctor.addFacility(facilityId);
        Doctor savedDoctor = doctorRepositoryPort.save(doctor);
        return doctorMapperPort.toDto(savedDoctor);
    }

    public DoctorDto removeFacilityFromDoctor(Long doctorId, Long facilityId) {
        Doctor doctor = doctorRepositoryPort.findById(doctorId)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));
        facilityRepositoryPort.findById(facilityId)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));

        doctor.removeFacility(facilityId);
        Doctor savedDoctor = doctorRepositoryPort.save(doctor);
        return doctorMapperPort.toDto(savedDoctor);
    }
}