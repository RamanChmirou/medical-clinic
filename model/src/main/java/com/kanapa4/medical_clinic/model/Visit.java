package com.kanapa4.medical_clinic.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visit {
    private Long id;
    private LocalDateTime dateTime;
    private Integer durationInMinutes;
    private Doctor doctor;
    private Patient patient;
    private Facility facility;
}
