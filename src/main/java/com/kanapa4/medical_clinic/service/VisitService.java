package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.model.dto.*;
import com.kanapa4.medical_clinic.model.entity.*;
import com.kanapa4.medical_clinic.repository.*;
import com.kanapa4.medical_clinic.mapper.VisitMapper;
import com.kanapa4.medical_clinic.exception.*;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public VisitDto createVisitSlot(VisitCreateCommand dto) {
        LocalDateTime appointmentTime = dto.getDateTime();
        Integer duration = dto.getDurationInMinutes();

        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new InvalidVisitException("Cannot create a visit in the past.");
        }

        if (appointmentTime.getMinute() % 15 != 0 || appointmentTime.getSecond() != 0) {
            throw new InvalidVisitException("Visits can only be scheduled on the quarter-hour (e.g., 14:00, 14:15).");
        }

        if (duration == null || duration <= 0 || duration % 15 != 0) {
            throw new InvalidVisitException("Visit duration must be a positive multiple of 15 minutes.");
        }

        LocalDateTime endTime = appointmentTime.plusMinutes(duration);

        LocalDateTime dayStart = appointmentTime.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<Visit> doctorsVisitsForDay = visitRepository.findAllByDoctorIdAndDate(dto.getDoctorId(), dayStart, dayEnd);

        boolean hasOverlap = doctorsVisitsForDay.stream().anyMatch(existingVisit -> {
            LocalDateTime existingStart = existingVisit.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existingVisit.getDurationInMinutes());

            return appointmentTime.isBefore(existingEnd) && existingStart.isBefore(endTime);
        });

        if (hasOverlap) {
            throw new VisitAlreadyExistsException("The doctor already has a visit scheduled that overlaps with this time interval.");
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist."));

        Visit visit = Visit.builder()
                .dateTime(appointmentTime)
                .durationInMinutes(duration)
                .doctor(doctor)
                .patient(null)
                .build();

        return visitMapper.toDto(visitRepository.save(visit));
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
}