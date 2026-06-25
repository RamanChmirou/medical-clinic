package com.kanapa4.medical_clinic.mapper;

import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.model.entity.Doctor;
import com.kanapa4.medical_clinic.model.entity.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    @Mapping(target = "doctorsId", expression = "java(mapDoctorsToIds(facility.getDoctors()))")
    FacilityDto toDto(Facility facility);

    Facility toEntity(FacilityDto facilityDto);

    default Set<Long> mapDoctorsToIds(Set<Doctor> doctors) {
        if (doctors == null) {
            return null;
        }
        return doctors.stream()
                .map(Doctor::getId)
                .collect(Collectors.toSet());
    }
    }
