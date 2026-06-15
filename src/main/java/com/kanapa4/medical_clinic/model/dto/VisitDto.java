package com.kanapa4.medical_clinic.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitDto {
    private Long id;
    private LocalDateTime dateTime;
    private Integer durationInMinutes;
    private Long doctorId;
    private Long patientId;

    public LocalDateTime getEndTime() {
        if (this.dateTime == null || this.durationInMinutes == null) {
            return null;
        }
        return this.dateTime.plusMinutes(this.durationInMinutes);
    }
}
