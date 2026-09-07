package com.kanapa4.medical_clinic.validator;

import com.kanapa4.medical_clinic.exception.InvalidVisitException;
import com.kanapa4.medical_clinic.exception.VisitAlreadyExistsException;
import com.kanapa4.medical_clinic.model.entity.Visit;
import com.kanapa4.medical_clinic.repository.VisitRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VisitValidator {
    private final VisitRepository visitRepository;

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

        List<Visit> doctorsVisitsForDay = visitRepository.findAllByDoctorIdAndDate(doctorId, dayStart, dayEnd);

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
