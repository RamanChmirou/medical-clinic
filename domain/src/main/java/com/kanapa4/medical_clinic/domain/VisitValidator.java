package com.kanapa4.medical_clinic.domain;

import com.kanapa4.medical_clinic.domain.port.VisitRepositoryPort;
import com.kanapa4.medical_clinic.model.Visit;
import com.kanapa4.medical_clinic.model.exception.InvalidVisitException;
import com.kanapa4.medical_clinic.model.exception.VisitAlreadyExistsException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class VisitValidator {
    private final VisitRepositoryPort visitRepositoryPort;

    public void validateVisitTimeAndDuration(LocalDateTime appointmentTime, Integer duration) {
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

    public void checkDoctorAvailability(Long doctorId, LocalDateTime appointmentTime, Integer duration, Long excludeVisitId) {
        LocalDateTime endTime = appointmentTime.plusMinutes(duration);
        LocalDateTime dayStart = appointmentTime.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<Visit> doctorsVisitsForDay = visitRepositoryPort.findAllByDoctorIdAndDate(doctorId, dayStart, dayEnd);

        boolean hasOverlap = doctorsVisitsForDay.stream()
                .filter(existingVisit -> !existingVisit.getId().equals(excludeVisitId))
                .anyMatch(existingVisit -> {
                    LocalDateTime existingStart = existingVisit.getDateTime();
                    LocalDateTime existingEnd = existingStart.plusMinutes(existingVisit.getDurationInMinutes());
                    return appointmentTime.isBefore(existingEnd) && existingStart.isBefore(endTime);
                });

        if (hasOverlap) {
            throw new VisitAlreadyExistsException("The doctor already has a visit scheduled that overlaps with this time interval.");
        }
    }
}
