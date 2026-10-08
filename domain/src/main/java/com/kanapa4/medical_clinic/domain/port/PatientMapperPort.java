package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Patient;
import com.kanapa4.medical_clinic.model.PatientDto;

public interface PatientMapperPort {
    Page<PatientDto> toPageDto(Page<Patient> patientPage);

    PatientDto toDto(Patient patient);
}
