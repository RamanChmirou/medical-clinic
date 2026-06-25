package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.model.dto.*;
import com.kanapa4.medical_clinic.model.entity.*;
import com.kanapa4.medical_clinic.repository.*;
import com.kanapa4.medical_clinic.mapper.VisitMapper;
import com.kanapa4.medical_clinic.exception.*;
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

        validateVisitTimeAndDuration(appointmentTime, duration);

        checkDoctorAvailability(doctorId, appointmentTime, duration);

        Doctor doctor = getDoctorById(doctorId);

        Visit visit = Visit.builder()
                .dateTime(appointmentTime)
                .durationInMinutes(duration)
                .doctor(doctor)
                .patient(null)
                .build();

        return visitMapper.toDto(visitRepository.save(visit));
    }

    @Transactional
    public VisitDto updateVisit(Long id, VisitCreateCommand dto) {
        Visit existingVisit = visitRepository.findById(id)
                .orElseThrow(() -> new VisitDoesNotExistsException("Visit does not exists."));

        LocalDateTime newAppointmentTime = dto.getDateTime();
        Integer newDuration = dto.getDurationInMinutes();
        Long newDoctorId = dto.getDoctorId();

        validateVisitTimeAndDuration(newAppointmentTime, newDuration);

        if (!existingVisit.getDoctor().getId().equals(newDoctorId) ||
                !existingVisit.getDateTime().equals(newAppointmentTime) ||
                !existingVisit.getDurationInMinutes().equals(newDuration)) {

            checkDoctorAvailability(newDoctorId, newAppointmentTime, newDuration);
        }

        if (!existingVisit.getDoctor().getId().equals(newDoctorId)) {
            Doctor newDoctor = getDoctorById(newDoctorId);
            existingVisit.setDoctor(newDoctor);
        }

        existingVisit.setDateTime(newAppointmentTime);
        existingVisit.setDurationInMinutes(newDuration);

        assignPatientToVisit(existingVisit, dto.getPatientId());

        return visitMapper.toDto(visitRepository.save(existingVisit));
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

    private void validateVisitTimeAndDuration(LocalDateTime appointmentTime, Integer duration) {
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new InvalidVisitException("Cannot create a visit in the past.");
        }

        if (appointmentTime.getMinute() % 15 != 0 || appointmentTime.getSecond() != 0) {
            throw new InvalidVisitException("Visits can only be scheduled on the quarter-hour (e.g., 14:00, 14:15).");
        }

        if (duration == null || duration <= 0 || duration % 15 != 0) {
            throw new InvalidVisitException("Visit duration must be a positive multiple of 15 minutes.");
        }
    }

    private void checkDoctorAvailability(Long doctorId, LocalDateTime appointmentTime, Integer duration) {
        LocalDateTime endTime = appointmentTime.plusMinutes(duration);

        LocalDateTime dayStart = appointmentTime.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<Visit> doctorsVisitsForDay = visitRepository.findAllByDoctorIdAndDate(doctorId, dayStart, dayEnd);

        boolean hasOverlap = doctorsVisitsForDay.stream().anyMatch(existingVisit -> {
            LocalDateTime existingStart = existingVisit.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existingVisit.getDurationInMinutes());

            return appointmentTime.isBefore(existingEnd) && existingStart.isBefore(endTime);
        });

        if (hasOverlap) {
            throw new VisitAlreadyExistsException("The doctor already has a visit scheduled that overlaps with this time interval.");
        }
    }

    private Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist."));
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
}
