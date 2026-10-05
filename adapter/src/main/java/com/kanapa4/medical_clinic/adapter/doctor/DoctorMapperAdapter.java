package com.kanapa4.medical_clinic.adapter.doctor;

import com.kanapa4.medical_clinic.domain.port.DoctorMapperPort;
import com.kanapa4.medical_clinic.model.Doctor;
import com.kanapa4.medical_clinic.model.DoctorDto;
import com.kanapa4.medical_clinic.model.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DoctorMapperAdapter implements DoctorMapperPort {
    private final DoctorMapper doctorMapper;

    @Override
    public DoctorDto toDto(Doctor doctor) {
        return doctorMapper.toDto(doctor);
    }

    @Override
    public Page<DoctorDto> toPageDto(Page<Doctor> page) {
        List<DoctorDto> dtos = page.getContent().stream()
                .map(doctorMapper::toDto)
                .toList();

        return Page.<DoctorDto>builder()
                .content(dtos)
                .page(page.getPage())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}