package com.kanapa4.medical_clinic.adapter.visit;

import com.kanapa4.medical_clinic.adapter.VisitEntity;
import com.kanapa4.medical_clinic.adapter.doctor.DoctorMapper;
import com.kanapa4.medical_clinic.adapter.facility.FacilityMapper;
import com.kanapa4.medical_clinic.adapter.patient.PatientMapper;
import com.kanapa4.medical_clinic.model.Visit;
import com.kanapa4.medical_clinic.model.VisitDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class, PatientMapper.class, FacilityMapper.class})
public interface VisitMapper {
    @Mapping(target = "doctor", source = "doctorEntity")
    @Mapping(target = "patient", source = "patientEntity")
    Visit toPojo(VisitEntity entity);

    @Mapping(target = "doctorEntity", ignore = true)
    @Mapping(target = "patientEntity", ignore = true)
    @Mapping(target = "facility", ignore = true)
    VisitEntity toEntity(Visit domain);

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "facilityId", source = "facility.id")
    VisitDto toDto(Visit visit);
}