package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.model.dto.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.model.entity.Facility;
import com.kanapa4.medical_clinic.repository.FacilityRepository;
import com.kanapa4.medical_clinic.mapper.FacilityMapper;
import com.kanapa4.medical_clinic.exception.FacilityDoesNotExistsException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public Page<FacilityDto> getPaginatedFacilities(int page, int size, String sortBy) {
        if (size > 30) {
            size = 30;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return facilityRepository.findAll(pageable).map(facilityMapper::toDto);
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
