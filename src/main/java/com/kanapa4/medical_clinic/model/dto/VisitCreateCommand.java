package com.kanapa4.medical_clinic.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VisitCreateCommand {
    private LocalDateTime dateTime;
    private Integer durationInMinutes;
    private Long doctorId;
}
