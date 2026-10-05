package com.kanapa4.medical_clinic.adapter.facility;

import com.kanapa4.medical_clinic.adapter.FacilityEntity;
import com.kanapa4.medical_clinic.domain.port.FacilityRepositoryPort;
import com.kanapa4.medical_clinic.model.Facility;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacilityRepositoryAdapter implements FacilityRepositoryPort {

    private final FacilityMapper facilityMapper;
    private final FacilityRepository facilityRepository;

    @Override
    public Page<Facility> findAll(Pageable pageable) {
        var springPageable = PageRequest.of(pageable.getPage(), pageable.getSize());
        var springPage = facilityRepository.findAll(springPageable);
        List<Facility> domainFacilities = springPage.getContent().stream()
                .map(facilityMapper::toPojo)
                .toList();

        return Page.<Facility>builder()
                .content(domainFacilities)
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .build();
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
        FacilityEntity entity;
        if (facility.getId() == null) {
            entity = facilityMapper.toEntity(facility);
        } else {
            entity = facilityRepository.findById(facility.getId()).orElseThrow();
            entity.setName(facility.getName());
            entity.setCity(facility.getCity());
            entity.setZipCode(facility.getZipCode());
            entity.setStreet(facility.getStreet());
            entity.setBuildingNumber(facility.getBuildingNumber());
        }
        FacilityEntity savedEntity = facilityRepository.save(entity);
        return facilityMapper.toPojo(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        FacilityEntity facility = facilityRepository.findById(id).orElseThrow();
        facility.getDoctors().forEach(doctor -> doctor.getFacilities().remove(facility));
        facilityRepository.delete(facility);
    }
}