package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.mapper.FacilityMapper;
import com.kanapa4.medical_clinic.model.dto.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.model.entity.Facility;
import com.kanapa4.medical_clinic.repository.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FacilityServiceTest {
    private FacilityRepository facilityRepository;
    private FacilityMapper facilityMapper;
    private FacilityService facilityService;

    @BeforeEach
    void setup() {
        this.facilityRepository = mock(FacilityRepository.class);
        this.facilityMapper = getMapper(FacilityMapper.class);
        this.facilityService = new FacilityService(facilityRepository, facilityMapper);
    }

    @Test
    void getPaginatedFacilities_DataCorrect_ReturnPaginatedFacilitiesDtos() {
        //given
        Pageable pageable = PageRequest.of(1, 25, Sort.by("name").ascending());
        Facility facility = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();

        Page<Facility> facilityPage = new PageImpl<>(List.of(facility), pageable, 1);
        when(facilityRepository.findAll(pageable)).thenReturn(facilityPage);
        //when
        Page<FacilityDto> result = facilityService.getPaginatedFacilities(1, 25, "name");
        //then
        FacilityDto toAssert = result.getContent().getFirst();
        assertAll(
                () -> assertEquals(2, result.getTotalPages()),
                () -> assertEquals(facility.getName(), toAssert.getName()),
                () -> assertEquals(facility.getId(), toAssert.getId()),
                () -> assertEquals(facility.getCity(), toAssert.getCity()),
                () -> assertEquals(facility.getZipCode(), toAssert.getZipCode()),
                () -> assertEquals(facility.getStreet(), toAssert.getStreet()),
                () -> assertEquals(facility.getBuildingNumber(), toAssert.getBuildingNumber())
        );
    }

    @Test
    void findById_DataCorrect_ReturnFoundFacilityDto() {
        //given
        Long id = 5L;
        Facility facility = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        when(facilityRepository.findById(id)).thenReturn(Optional.of(facility));
        //when
        FacilityDto result = facilityService.findById(id);
        //then
        assertAll(
                () -> assertEquals(facility.getName(), result.getName()),
                () -> assertEquals(facility.getId(), result.getId()),
                () -> assertEquals(facility.getCity(), result.getCity()),
                () -> assertEquals(facility.getZipCode(), result.getZipCode()),
                () -> assertEquals(facility.getStreet(), result.getStreet()),
                () -> assertEquals(facility.getBuildingNumber(), result.getBuildingNumber())
        );
    }

    @Test
    void create_DataCorrect_ReturnCreatedFacility() {
        //given
        FacilityCreateCommand facilityCreateCommand = FacilityCreateCommand.builder()
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        Facility facility = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        when(facilityRepository.save(any(Facility.class))).thenReturn(facility);
        //when
        FacilityDto result = facilityService.create(facilityCreateCommand);
        //then
        assertAll(
                () -> assertEquals(facilityCreateCommand.getName(), result.getName()),
                () -> assertEquals(facilityCreateCommand.getCity(), result.getCity()),
                () -> assertEquals(facilityCreateCommand.getZipCode(), result.getZipCode()),
                () -> assertEquals(facilityCreateCommand.getStreet(), result.getStreet()),
                () -> assertEquals(facilityCreateCommand.getBuildingNumber(), result.getBuildingNumber())
        );
    }

    @Test
    void update_DataCorrect_ReturnUpdatedFacility() {
        //given
        Long id = 5L;
        Facility facilityToUpdate = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        FacilityDto facilityDto = FacilityDto.builder()
                .id(5L)
                .name("navi")
                .city("Cologne")
                .zipCode("007-00")
                .street("bl1ad")
                .buildingNumber("21")
                .build();
        Facility updatedFacility = Facility.builder()
                .id(5L)
                .name("navi")
                .city("Cologne")
                .zipCode("007-00")
                .street("bl1ad")
                .buildingNumber("21")
                .build();
        when(facilityRepository.findById(id)).thenReturn(Optional.of(facilityToUpdate));
        when(facilityRepository.save(any(Facility.class))).thenReturn(updatedFacility);
        //when
        FacilityDto result = facilityService.update(id, facilityDto);
        //then
        assertAll(
                () -> assertEquals(facilityToUpdate.getName(), result.getName()),
                () -> assertEquals(facilityToUpdate.getCity(), result.getCity()),
                () -> assertEquals(facilityToUpdate.getZipCode(), result.getZipCode()),
                () -> assertEquals(facilityToUpdate.getStreet(), result.getStreet()),
                () -> assertEquals(facilityToUpdate.getBuildingNumber(), result.getBuildingNumber())
        );
    }
}