package com.kanapa4.medical_clinic.domain;

import com.kanapa4.medical_clinic.domain.port.*;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.*;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class VisitService {
    private final VisitRepositoryPort visitRepositoryPort;
    private final DoctorRepositoryPort doctorRepositoryPort;
    private final PatientRepositoryPort patientRepositoryPort;
    private final VisitMapperPort visitMapperPort;
    private final FacilityRepositoryPort facilityRepositoryPort;
    private final VisitValidator visitValidator;

    public Page<VisitDto> getPaginatedVisits(Pageable pageable) {
        return visitMapperPort.toPageDto(visitRepositoryPort.findAll(pageable));
    }

    public Page<VisitDto> searchVisits(VisitFilter filter, Pageable pageable) {
        return visitMapperPort.toPageDto(visitRepositoryPort.findAll(filter, pageable));
    }

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
        Visit savedVisit = visitRepositoryPort.save(visit);
        return visitMapperPort.toDto(savedVisit);
    }

    public VisitDto updateVisit(Long id, VisitCreateCommand dto) {
        Visit existingVisit = visitRepositoryPort.findById(id)
                .orElseThrow(() -> new VisitDoesNotExistsException("Visit does not exist."));

        visitValidator.validateVisitTimeAndDuration(dto.getDateTime(), dto.getDurationInMinutes());
        visitValidator.checkDoctorAvailability(dto.getDoctorId(), dto.getDateTime(), dto.getDurationInMinutes(), id);

        existingVisit.setDateTime(dto.getDateTime());
        existingVisit.setDurationInMinutes(dto.getDurationInMinutes());

        updateDoctorIfNeeded(existingVisit, dto.getDoctorId());
        assignPatientToVisit(existingVisit, dto.getPatientId());

        Visit savedVisit = visitRepositoryPort.save(existingVisit);
        return visitMapperPort.toDto(savedVisit);
    }

    public void delete(Long id) {
        if (!visitRepositoryPort.existsById(id)) {
            throw new VisitDoesNotExistsException("Visit does not exist.");
        }
        visitRepositoryPort.deleteById(id);
    }

    public VisitDto bookVisit(Long visitId, Long patientId) {
        Visit visit = visitRepositoryPort.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistsException("Visit does not exist."));

        if (visit.getDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidVisitException("Cannot book a visit in the past.");
        }
        if (visit.getPatient() != null) {
            throw new VisitUnavailableException("This visit is already booked.");
        }
        Patient patient = patientRepositoryPort.findById(patientId)
                .orElseThrow(() -> new PatientDoesNotExistsException("Patient does not exist."));

        visit.setPatient(patient);
        Visit savedVisit = visitRepositoryPort.save(visit);
        return visitMapperPort.toDto(savedVisit);
    }

    public VisitDto cancelVisit(Long visitId) {
        Visit visit = visitRepositoryPort.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistsException("Visit does not exist"));
        if (visit.getPatient() == null) {
            throw new VisitUnavailableException("Cannot cancel a visit that has no patient booked.");
        }
        visit.setPatient(null);
        return visitMapperPort.toDto(visitRepositoryPort.save(visit));
    }

    private Doctor getDoctorById(Long doctorId) {
        return doctorRepositoryPort.findById(doctorId)
                .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist."));
    }

    private Facility getFacilityById(Long facilityId) {
        return facilityRepositoryPort.findById(facilityId)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist."));
    }

    private void assignPatientToVisit(Visit visit, Long patientId) {
        if (patientId != null) {
            Patient patient = patientRepositoryPort.findById(patientId)
                    .orElseThrow(() -> new PatientDoesNotExistsException("Patient not found."));
            visit.setPatient(patient);
        } else {
            visit.setPatient(null);
        }
    }

    private void updateDoctorIfNeeded(Visit visit, Long newDoctorId) {
        if (!visit.getDoctor().getId().equals(newDoctorId)) {
            Doctor newDoctor = doctorRepositoryPort.findById(newDoctorId)
                    .orElseThrow(() -> new DoctorDoesNotExistsException("Doctor does not exist."));
            visit.setDoctor(newDoctor);
        }
    }
}