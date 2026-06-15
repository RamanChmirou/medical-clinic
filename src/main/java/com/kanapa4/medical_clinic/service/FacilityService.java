package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.model.dto.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.model.entity.Facility;
import com.kanapa4.medical_clinic.repository.FacilityRepository;
import com.kanapa4.medical_clinic.mapper.FacilityMapper;
import com.kanapa4.medical_clinic.exception.FacilityDoesNotExistsException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public List<FacilityDto> findAll() {
        return facilityRepository.findAll().stream()
                .map(facilityMapper::toDto)
                .collect(Collectors.toList());
    }

    public FacilityDto findById(Long id) {
        return facilityRepository.findById(id)
                .map(facilityMapper::toDto)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));
    }

    @Transactional
    public FacilityDto create(FacilityCreateCommand command) {
        Facility facility = Facility.create(command);

        Facility savedFacility = facilityRepository.save(facility);
        return facilityMapper.toDto(savedFacility);
    }

    @Transactional
    public FacilityDto update(Long id, FacilityDto dto) {
        Facility existing = facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));

        existing.update(dto);

        Facility savedFacility = facilityRepository.save(existing);
        return facilityMapper.toDto(savedFacility);
    }

    @Transactional
    public void delete(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility does not exist"));

        facility.getDoctors().forEach(doctor -> doctor.getFacilities().remove(facility));

        facilityRepository.deleteById(id);
    }
}
