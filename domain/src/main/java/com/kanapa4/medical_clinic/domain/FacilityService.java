package com.kanapa4.medical_clinic.domain;

import com.kanapa4.medical_clinic.domain.port.FacilityMapperPort;
import com.kanapa4.medical_clinic.domain.port.FacilityRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.FacilityAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.FacilityDoesNotExistsException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepositoryPort facilityRepositoryPort;
    private final FacilityMapperPort facilityMapperPort;

    public Page<FacilityDto> getPaginatedFacilities(Pageable pageable) {
        return facilityMapperPort.toPageDto(facilityRepositoryPort.findAll(pageable));
    }

    public FacilityDto findById(Long id) {
        return facilityRepositoryPort.findById(id)
                .map(facilityMapperPort::toDto)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));
    }

    public FacilityDto create(FacilityCreateCommand command) {
        if (facilityRepositoryPort.findByName(command.getName()).isPresent()) {
            throw new FacilityAlreadyExistsException("Facility already exists");
        }
        Facility facility = Facility.create(command);
        Facility savedFacility = facilityRepositoryPort.save(facility);
        return facilityMapperPort.toDto(savedFacility);
    }

    public FacilityDto update(Long id, FacilityDto dto) {
        Facility existing = facilityRepositoryPort.findById(id)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));
        existing.update(dto);

        Facility savedFacility = facilityRepositoryPort.save(existing);
        return facilityMapperPort.toDto(savedFacility);
    }

    public void delete(Long id) {
        facilityRepositoryPort.findById(id)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));
        facilityRepositoryPort.deleteById(id);
    }
}