package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Facility;
import com.kanapa4.medical_clinic.model.FacilityDto;
import com.kanapa4.medical_clinic.model.Page;

public interface FacilityMapperPort {
    FacilityDto toDto(Facility facility);

    Page<FacilityDto> toPageDto(Page<Facility> facilityPage);
}
