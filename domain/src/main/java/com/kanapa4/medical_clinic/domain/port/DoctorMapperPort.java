package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Doctor;
import com.kanapa4.medical_clinic.model.DoctorDto;
import com.kanapa4.medical_clinic.model.Page;

public interface DoctorMapperPort {
    DoctorDto toDto(Doctor doctor);

    Page<DoctorDto> toPageDto(Page<Doctor> page);
}