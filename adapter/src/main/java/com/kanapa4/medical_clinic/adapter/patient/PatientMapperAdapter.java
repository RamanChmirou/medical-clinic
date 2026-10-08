package com.kanapa4.medical_clinic.adapter.patient;

import com.kanapa4.medical_clinic.domain.port.PatientMapperPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Patient;
import com.kanapa4.medical_clinic.model.PatientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientMapperAdapter implements PatientMapperPort {

    private final PatientMapper patientMapper;

    @Override
    public PatientDto toDto(Patient patient) {
        return patientMapper.toDto(patient);
    }

    @Override
    public Page<PatientDto> toPageDto(Page<Patient> patientPage) {
        return patientPage == null ? null : patientPage.map(this::toDto);
    }
}