package com.kanapa4.medical_clinic;

import com.kanapa4.medical_clinic.domain.FacilityService;
import com.kanapa4.medical_clinic.domain.port.FacilityMapperPort;
import com.kanapa4.medical_clinic.domain.port.FacilityRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.FacilityAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.FacilityDoesNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FacilityServiceTest {

    private FacilityRepositoryPort facilityRepositoryPort;
    private FacilityMapperPort facilityMapperPort;
    private FacilityService facilityService;

    @BeforeEach
    void setup() {
        this.facilityRepositoryPort = mock(FacilityRepositoryPort.class);
        this.facilityMapperPort = mock(FacilityMapperPort.class);
        this.facilityService = new FacilityService(facilityRepositoryPort, facilityMapperPort);
    }

    @Test
    void getPaginatedFacilities_DataCorrect_ReturnPaginatedFacilitiesDtos() {
        // given
        Pageable pageable = new Pageable(1, 25);
        Facility facility = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        FacilityDto facilityDto = FacilityDto.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();

        Page<Facility> facilityPage = Page.<Facility>builder()
                .content(List.of(facility))
                .page(1)
                .size(25)
                .totalElements(26L)
                .totalPages(2)
                .build();

        Page<FacilityDto> expectedDtoPage = Page.<FacilityDto>builder()
                .content(List.of(facilityDto))
                .page(1)
                .size(25)
                .totalElements(26L)
                .totalPages(2)
                .build();

        when(facilityRepositoryPort.findAll(pageable)).thenReturn(facilityPage);
        when(facilityMapperPort.toPageDto(facilityPage)).thenReturn(expectedDtoPage);

        // when
        Page<FacilityDto> result = facilityService.getPaginatedFacilities(pageable);

        // then
        FacilityDto toAssert = result.getContent().getFirst();
        assertAll(
                () -> assertEquals(2, result.getTotalPages()),
                () -> assertEquals(facilityDto.getName(), toAssert.getName()),
                () -> assertEquals(facilityDto.getId(), toAssert.getId()),
                () -> assertEquals(facilityDto.getCity(), toAssert.getCity()),
                () -> assertEquals(facilityDto.getZipCode(), toAssert.getZipCode()),
                () -> assertEquals(facilityDto.getStreet(), toAssert.getStreet()),
                () -> assertEquals(facilityDto.getBuildingNumber(), toAssert.getBuildingNumber())
        );
        verify(facilityRepositoryPort, times(1)).findAll(pageable);
        verify(facilityMapperPort, times(1)).toPageDto(facilityPage);
        verifyNoMoreInteractions(facilityRepositoryPort, facilityMapperPort);
    }

    @Test
    void findById_DataCorrect_ReturnFoundFacilityDto() {
        // given
        Long id = 5L;
        Facility facility = Facility.builder()
                .id(id)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        FacilityDto expectedDto = FacilityDto.builder()
                .id(id)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();

        when(facilityRepositoryPort.findById(id)).thenReturn(Optional.of(facility));
        when(facilityMapperPort.toDto(facility)).thenReturn(expectedDto);

        // when
        FacilityDto result = facilityService.findById(id);

        // then
        assertAll(
                () -> assertEquals(expectedDto.getName(), result.getName()),
                () -> assertEquals(expectedDto.getId(), result.getId()),
                () -> assertEquals(expectedDto.getCity(), result.getCity()),
                () -> assertEquals(expectedDto.getZipCode(), result.getZipCode()),
                () -> assertEquals(expectedDto.getStreet(), result.getStreet()),
                () -> assertEquals(expectedDto.getBuildingNumber(), result.getBuildingNumber())
        );
        verify(facilityRepositoryPort, times(1)).findById(id);
        verify(facilityMapperPort, times(1)).toDto(facility);
        verifyNoMoreInteractions(facilityRepositoryPort, facilityMapperPort);
    }

    @Test
    void create_DataCorrect_ReturnCreatedFacility() {
        // given
        FacilityCreateCommand command = FacilityCreateCommand.builder()
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        Facility savedFacility = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        FacilityDto expectedDto = FacilityDto.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();

        when(facilityRepositoryPort.findByName(command.getName())).thenReturn(Optional.empty());
        when(facilityRepositoryPort.save(any(Facility.class))).thenReturn(savedFacility);
        when(facilityMapperPort.toDto(savedFacility)).thenReturn(expectedDto);

        // when
        FacilityDto result = facilityService.create(command);

        // then
        assertAll(
                () -> assertEquals(command.getName(), result.getName()),
                () -> assertEquals(command.getCity(), result.getCity()),
                () -> assertEquals(command.getZipCode(), result.getZipCode()),
                () -> assertEquals(command.getStreet(), result.getStreet()),
                () -> assertEquals(command.getBuildingNumber(), result.getBuildingNumber())
        );
        verify(facilityRepositoryPort, times(1)).findByName(command.getName());
        verify(facilityRepositoryPort, times(1)).save(any(Facility.class));
        verify(facilityMapperPort, times(1)).toDto(savedFacility);
        verifyNoMoreInteractions(facilityRepositoryPort, facilityMapperPort);
    }

    @Test
    void update_DataCorrect_ReturnUpdatedFacility() {
        // given
        Long id = 5L;
        Facility existingFacility = Facility.builder()
                .id(id)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        FacilityDto updateDto = FacilityDto.builder()
                .id(id)
                .name("navi")
                .city("Cologne")
                .zipCode("007-00")
                .street("bl1ad")
                .buildingNumber("21")
                .build();
        Facility savedFacility = Facility.builder()
                .id(id)
                .name("navi")
                .city("Cologne")
                .zipCode("007-00")
                .street("bl1ad")
                .buildingNumber("21")
                .build();

        when(facilityRepositoryPort.findById(id)).thenReturn(Optional.of(existingFacility));
        when(facilityRepositoryPort.save(existingFacility)).thenReturn(savedFacility);
        when(facilityMapperPort.toDto(savedFacility)).thenReturn(updateDto);

        // when
        FacilityDto result = facilityService.update(id, updateDto);

        // then
        assertAll(
                () -> assertEquals(updateDto.getName(), result.getName()),
                () -> assertEquals(updateDto.getCity(), result.getCity()),
                () -> assertEquals(updateDto.getZipCode(), result.getZipCode()),
                () -> assertEquals(updateDto.getStreet(), result.getStreet()),
                () -> assertEquals(updateDto.getBuildingNumber(), result.getBuildingNumber())
        );
        verify(facilityRepositoryPort, times(1)).findById(id);
        verify(facilityRepositoryPort, times(1)).save(existingFacility);
        verify(facilityMapperPort, times(1)).toDto(savedFacility);
        verifyNoMoreInteractions(facilityRepositoryPort, facilityMapperPort);
    }

    @Test
    void findById_FacilityDoesNotExists_ThrowFacilityDoesNotExistsException() {
        // given
        Long id = 1L;
        when(facilityRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> facilityService.findById(id));

        assertEquals("Facility does not exist", result.getMessage());
    }

    @Test
    void create_FacilityAlreadyExists_ThrowFacilityAlreadyExistsException() {
        // given
        FacilityCreateCommand command = FacilityCreateCommand.builder()
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        Facility existingFacility = Facility.builder()
                .id(5L)
                .name("falcons")
                .build();
        when(facilityRepositoryPort.findByName(command.getName())).thenReturn(Optional.of(existingFacility));

        // when & then
        FacilityAlreadyExistsException result = assertThrows(FacilityAlreadyExistsException.class,
                () -> facilityService.create(command));

        assertEquals("Facility already exists", result.getMessage());
    }

    @Test
    void update_FacilityDoesNotExists_ThrowFacilityDoesNotExistsException() {
        // given
        Long id = 1L;
        FacilityDto updateDto = FacilityDto.builder()
                .id(id)
                .name("navi")
                .build();
        when(facilityRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> facilityService.update(id, updateDto));

        assertEquals("Facility does not exist", result.getMessage());
    }

    @Test
    void delete_DataCorrect_DeleteFacility() {
        // given
        Long id = 1L;
        Facility facility = Facility.builder()
                .id(id)
                .build();
        when(facilityRepositoryPort.findById(id)).thenReturn(Optional.of(facility));

        // when
        facilityService.delete(id);

        // then
        verify(facilityRepositoryPort, times(1)).findById(id);
        verify(facilityRepositoryPort, times(1)).deleteById(id);
        verifyNoMoreInteractions(facilityRepositoryPort);
    }

    @Test
    void delete_FacilityDoesNotExists_ThrowFacilityDoesNotExistsException() {
        // given
        Long id = 1L;
        when(facilityRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> facilityService.delete(id));

        assertEquals("Facility does not exist", result.getMessage());
    }
}