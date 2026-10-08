package com.kanapa4.medical_clinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitCreateCommand {
    private LocalDateTime dateTime;
    private Integer durationInMinutes;
    private Long doctorId;
    private Long patientId;
    private Long facilityId;
}
