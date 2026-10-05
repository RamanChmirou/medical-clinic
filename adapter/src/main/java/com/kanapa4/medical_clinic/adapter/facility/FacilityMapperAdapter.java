package com.kanapa4.medical_clinic.adapter.facility;

import com.kanapa4.medical_clinic.domain.port.FacilityMapperPort;
import com.kanapa4.medical_clinic.model.Facility;
import com.kanapa4.medical_clinic.model.FacilityDto;
import com.kanapa4.medical_clinic.model.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacilityMapperAdapter implements FacilityMapperPort {

    private final FacilityMapper facilityMapper;

    @Override
    public FacilityDto toDto(Facility facility) {
        return facilityMapper.toDto(facility);
    }

    @Override
    public Page<FacilityDto> toPageDto(Page<Facility> facilityPage) {
        if (facilityPage == null) {
            return null;
        }
        List<FacilityDto> dtos = (facilityPage.getContent() == null)
                ? List.of()
                : facilityPage.getContent().stream()
                  .map(this::toDto)
                  .toList();
        return Page.<FacilityDto>builder()
                .content(dtos)
                .page(facilityPage.getPage())
                .size(facilityPage.getSize())
                .totalElements(facilityPage.getTotalElements())
                .totalPages(facilityPage.getTotalPages())
                .build();
    }
}
