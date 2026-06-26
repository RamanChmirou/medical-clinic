package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.*;
import com.kanapa4.medical_clinic.mapper.DoctorMapper;
import com.kanapa4.medical_clinic.mapper.FacilityMapper;
import com.kanapa4.medical_clinic.model.dto.DoctorCreateCommand;
import com.kanapa4.medical_clinic.model.dto.DoctorDto;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.model.entity.Doctor;
import com.kanapa4.medical_clinic.model.entity.Facility;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.DoctorRepository;
import com.kanapa4.medical_clinic.repository.FacilityRepository;
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
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final DoctorMapper doctorMapper;

    public Page<DoctorDto> getPaginatedDoctors(int page, int size, String sortBy) {
        if (size > 30) {
            size = 30;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return doctorRepository.findAll(pageable).map(doctorMapper::toDto);
    }

    public DoctorDto findById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));
        return doctorMapper.toDto(doctor);
    }

    public DoctorDto create(DoctorCreateCommand dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exist"));

        if (doctorRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new DoctorAlreadyExistsException("This user is already a doctor");
        }

        Doctor doctor = Doctor.create(dto, user);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toDto(savedDoctor);
    }

    @Transactional
    public DoctorDto update(Long id, DoctorDto dto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setSpecialization(dto.getSpecialization());

        return doctorMapper.toDto(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (doctorRepository.findById(id).isEmpty()) {
            throw new DoctorDoesNotExistsException("Doctor does not exist");
        }
        doctorRepository.deleteById(id);
    }

    @Transactional
    public DoctorDto addFacilityToDoctor(Long doctorId, Long facilityId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));

        doctor.addFacility(facility);

        return doctorMapper.toDto(doctor);
    }

    @Transactional
    public DoctorDto removeFacilityFromDoctor(Long doctorId, Long facilityId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist"));
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));

        doctor.removeFacility(facility);

        return doctorMapper.toDto(doctor);
    }
}
