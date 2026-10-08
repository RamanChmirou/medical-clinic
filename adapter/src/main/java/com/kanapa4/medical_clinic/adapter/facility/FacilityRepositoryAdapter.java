package com.kanapa4.medical_clinic.adapter.facility;

import com.kanapa4.medical_clinic.adapter.FacilityEntity;
import com.kanapa4.medical_clinic.adapter.PageMapper;
import com.kanapa4.medical_clinic.domain.port.FacilityRepositoryPort;
import com.kanapa4.medical_clinic.model.Facility;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.exception.FacilityAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.FacilityDoesNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacilityRepositoryAdapter implements FacilityRepositoryPort {

    private final FacilityMapper facilityMapper;
    private final FacilityRepository facilityRepository;

    @Override
    public Page<Facility> findAll(Pageable pageable) {
        var springPage = facilityRepository.findAll(PageMapper.toSpringPageable(pageable));
        return PageMapper.toPojoPage(springPage, facilityMapper::toPojo);
    }

    @Override
    public Optional<Facility> findById(Long id) {
        return facilityRepository.findById(id)
                .map(facilityMapper::toPojo);
    }

    @Override
    public Optional<Facility> findByName(String name) {
        return facilityRepository.findByName(name)
                .map(facilityMapper::toPojo);
    }

    @Override
    @Transactional
    public Facility save(Facility facility) {
        facilityRepository.findByName(facility.getName())
                .filter(existing -> !existing.getId().equals(facility.getId()))
                .ifPresent(existing -> {
                    throw new FacilityAlreadyExistsException("Facility with this name already exists");
                });

        FacilityEntity entity = facilityMapper.toEntity(facility);
        FacilityEntity savedEntity = facilityRepository.save(entity);
        return facilityMapper.toPojo(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        FacilityEntity facility = facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityDoesNotExistsException("Facility with this id does not exists."));
        facility.getDoctors().forEach(doctor -> doctor.getFacilities().remove(facility));
        facilityRepository.delete(facility);
    }
}