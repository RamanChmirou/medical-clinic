package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.*;
import com.kanapa4.medical_clinic.mapper.VisitMapper;
import com.kanapa4.medical_clinic.model.dto.VisitCreateCommand;
import com.kanapa4.medical_clinic.model.dto.VisitDto;
import com.kanapa4.medical_clinic.model.entity.Doctor;
import com.kanapa4.medical_clinic.model.entity.Facility;
import com.kanapa4.medical_clinic.model.entity.Patient;
import com.kanapa4.medical_clinic.model.entity.Visit;
import com.kanapa4.medical_clinic.repository.DoctorRepository;
import com.kanapa4.medical_clinic.repository.FacilityRepository;
import com.kanapa4.medical_clinic.repository.PatientRepository;
import com.kanapa4.medical_clinic.repository.VisitRepository;
import com.kanapa4.medical_clinic.validator.VisitValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitMapper visitMapper;
    private final FacilityRepository facilityRepository;
    private final VisitValidator visitValidator;

    public Page<VisitDto> getPaginatedVisits(int page, int size, String sortBy) {
        if (size > 30) {
            size = 30;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return visitRepository.findAll(pageable).map(visitMapper::toDto);
    }

    @Transactional
    public VisitDto createVisitSlot(VisitCreateCommand dto) {
        LocalDateTime appointmentTime = dto.getDateTime();
        Integer duration = dto.getDurationInMinutes();
        Long doctorId = dto.getDoctorId();

        visitValidator.validateVisitTimeAndDuration(dto.getDateTime(), dto.getDurationInMinutes());
        visitValidator.checkDoctorAvailability(dto.getDoctorId(), dto.getDateTime(), dto.getDurationInMinutes(), null);

        Doctor doctor = getDoctorById(doctorId);
        Facility facility = getFacilityById(dto.getFacilityId());

        Visit visit = Visit.builder()
                .dateTime(appointmentTime)
                .durationInMinutes(duration)
                .doctor(doctor)
                .patient(null)
                .facility(facility)
                .build();
        Visit savedVisit = visitRepository.save(visit);
        return visitMapper.toDto(savedVisit);
    }

    @Transactional
    public VisitDto updateVisit(Long id, VisitCreateCommand dto) {
        Visit existingVisit = visitRepository.findById(id)
                .orElseThrow(() -> new VisitDoesNotExistsException("Visit does not exist."));

        visitValidator.validateVisitTimeAndDuration(dto.getDateTime(), dto.getDurationInMinutes());
        visitValidator.checkDoctorAvailability(dto.getDoctorId(), dto.getDateTime(), dto.getDurationInMinutes(), id);

        existingVisit.setDateTime(dto.getDateTime());
        existingVisit.setDurationInMinutes(dto.getDurationInMinutes());

        updateDoctorIfNeeded(existingVisit, dto.getDoctorId());
        assignPatientToVisit(existingVisit, dto.getPatientId());

        Visit savedVisit = visitRepository.save(existingVisit);
        return visitMapper.toDto(savedVisit);
    }

    @Transactional
    public void deleteVisit(Long id) {
        if (!visitRepository.existsById(id)) {
            throw new VisitDoesNotExistsException("Visit does not exist.");
        }

        visitRepository.deleteById(id);
    }

    @Transactional
    public VisitDto bookVisit(Long visitId, Long patientId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistsException("Visit does not exist."));

        if (visit.getDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidVisitException("Cannot book a visit in the past.");
        }
        if (visit.getPatient() != null) {
            throw new VisitUnavailableException("This visit is already booked.");
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist."));

        visit.setPatient(patient);

        return visitMapper.toDto(visit);
    }

    public List<VisitDto> getPatientVisits(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientDoesNotExistsException("Patient does not exist.");
        }

        return visitRepository.findAllByPatientId(patientId).stream()
                .map(visitMapper::toDto)
                .collect(Collectors.toList());
    }

    private Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist."));
    }

    private Facility getFacilityById(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist."));
    }

    private void assignPatientToVisit(Visit visit, Long patientId) {
        if (patientId != null) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new PatientDoesNotExistsException("Patient not found."));
            visit.setPatient(patient);
        } else {
            visit.setPatient(null);
        }
    }

    private void updateDoctorIfNeeded(Visit visit, Long newDoctorId) {
        if (!visit.getDoctor().getId().equals(newDoctorId)) {
            Doctor newDoctor = doctorRepository.findById(newDoctorId)
                    .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist."));
            visit.setDoctor(newDoctor);
        }
    }
}
