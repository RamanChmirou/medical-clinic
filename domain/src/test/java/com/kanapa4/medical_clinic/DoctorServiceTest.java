package com.kanapa4.medical_clinic;

import com.kanapa4.medical_clinic.domain.DoctorService;
import com.kanapa4.medical_clinic.domain.port.DoctorMapperPort;
import com.kanapa4.medical_clinic.domain.port.DoctorRepositoryPort;
import com.kanapa4.medical_clinic.domain.port.FacilityRepositoryPort;
import com.kanapa4.medical_clinic.domain.port.UserRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.DoctorAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.DoctorDoesNotExistsException;
import com.kanapa4.medical_clinic.model.exception.FacilityDoesNotExistsException;
import com.kanapa4.medical_clinic.model.exception.UserDoesNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    private DoctorRepositoryPort doctorRepositoryPort;
    private UserRepositoryPort userRepositoryPort;
    private FacilityRepositoryPort facilityRepositoryPort;
    private DoctorMapperPort doctorMapperPort;
    private DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.doctorRepositoryPort = mock(DoctorRepositoryPort.class);
        this.userRepositoryPort = mock(UserRepositoryPort.class);
        this.facilityRepositoryPort = mock(FacilityRepositoryPort.class);
        this.doctorMapperPort = mock(DoctorMapperPort.class);
        this.doctorService = new DoctorService(doctorRepositoryPort, userRepositoryPort, facilityRepositoryPort, doctorMapperPort);
    }

    @Test
    void getPaginatedDoctors_DataCorrect_ReturnPaginatedDoctorDtos() {
        // given
        Pageable pageable = new Pageable(0, 10);
        Doctor doctor = Doctor.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .build();
        DoctorDto doctorDto = DoctorDto.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .build();

        Page<Doctor> doctorPage = Page.<Doctor>builder()
                .content(List.of(doctor))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        Page<DoctorDto> expectedDtoPage = Page.<DoctorDto>builder()
                .content(List.of(doctorDto))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(doctorRepositoryPort.findAll(pageable)).thenReturn(doctorPage);
        when(doctorMapperPort.toPageDto(doctorPage)).thenReturn(expectedDtoPage);

        // when
        Page<DoctorDto> result = doctorService.getPaginatedDoctors(pageable, null);

        // then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(doctor.getFirstName(), result.getContent().getFirst().getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getContent().getFirst().getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getContent().getFirst().getSpecialization())
        );
        verify(doctorRepositoryPort, times(1)).findAll(pageable);
        verify(doctorMapperPort, times(1)).toPageDto(doctorPage);
        verifyNoMoreInteractions(doctorRepositoryPort, doctorMapperPort);
    }

    @Test
    void getPaginatedDoctorsWithSpecialization_DataCorrect_ReturnPaginatedDoctorDtos() {
        // given
        Pageable pageable = new Pageable(0, 10);
        Doctor doctor = Doctor.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .build();
        DoctorDto doctorDto = DoctorDto.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .build();

        Page<Doctor> doctorPage = Page.<Doctor>builder()
                .content(List.of(doctor))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        Page<DoctorDto> expectedDtoPage = Page.<DoctorDto>builder()
                .content(List.of(doctorDto))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(doctorRepositoryPort.findBySpecialization(Specialization.CARDIOLOGY, pageable)).thenReturn(doctorPage);
        when(doctorMapperPort.toPageDto(doctorPage)).thenReturn(expectedDtoPage);

        // when
        Page<DoctorDto> result = doctorService.getPaginatedDoctors(pageable, Specialization.CARDIOLOGY);

        // then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(doctor.getFirstName(), result.getContent().getFirst().getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getContent().getFirst().getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getContent().getFirst().getSpecialization())
        );
        verify(doctorRepositoryPort, times(1)).findBySpecialization(Specialization.CARDIOLOGY, pageable);
        verify(doctorMapperPort, times(1)).toPageDto(doctorPage);
        verifyNoMoreInteractions(doctorRepositoryPort, doctorMapperPort);
    }

    @Test
    void findById_DataCorrect_ReturnFoundDoctorDto() {
        // given
        Long id = 1L;
        Doctor doctor = Doctor.builder()
                .id(id)
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .build();
        DoctorDto expectedDto = DoctorDto.builder()
                .id(id)
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .build();

        when(doctorRepositoryPort.findById(id)).thenReturn(Optional.of(doctor));
        when(doctorMapperPort.toDto(doctor)).thenReturn(expectedDto);

        // when
        DoctorDto result = doctorService.findById(id);

        // then
        assertAll(
                () -> assertEquals(doctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getSpecialization())
        );
        verify(doctorRepositoryPort, times(1)).findById(id);
        verify(doctorMapperPort, times(1)).toDto(doctor);
        verifyNoMoreInteractions(doctorRepositoryPort, doctorMapperPort);
    }

    @Test
    void create_DataCorrect_ReturnCreatedDoctorDto() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        DoctorCreateCommand command = DoctorCreateCommand.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .userId(1L)
                .build();
        Doctor savedDoctor = Doctor.builder()
                .id(1L)
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .userId(1L)
                .build();
        DoctorDto expectedDto = DoctorDto.builder()
                .id(1L)
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .build();

        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(doctorRepositoryPort.findByUserId(command.getUserId())).thenReturn(Optional.empty());
        when(doctorRepositoryPort.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(doctorMapperPort.toDto(savedDoctor)).thenReturn(expectedDto);

        // when
        DoctorDto result = doctorService.create(command);

        // then
        assertAll(
                () -> assertEquals(command.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(command.getFirstName(), result.getFirstName()),
                () -> assertEquals(command.getLastName(), result.getLastName())
        );
        verify(userRepositoryPort, times(1)).findById(1L);
        verify(doctorRepositoryPort, times(1)).findByUserId(command.getUserId());
        verify(doctorRepositoryPort, times(1)).save(any(Doctor.class));
        verify(doctorMapperPort, times(1)).toDto(savedDoctor);
        verifyNoMoreInteractions(doctorRepositoryPort, userRepositoryPort, doctorMapperPort);
    }

    @Test
    void update_DataCorrect_ReturnUpdatedDoctorDto() {
        // given
        Long id = 1L;
        DoctorDto doctorDto = DoctorDto.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("Nikola")
                .lastName("Kovach")
                .build();
        Doctor doctorToUpdate = Doctor.builder()
                .id(id)
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .build();
        Doctor updatedDoctor = Doctor.builder()
                .id(id)
                .specialization(Specialization.CARDIOLOGY)
                .firstName("Nikola")
                .lastName("Kovach")
                .build();

        when(doctorRepositoryPort.findById(id)).thenReturn(Optional.of(doctorToUpdate));
        when(doctorRepositoryPort.save(any(Doctor.class))).thenReturn(updatedDoctor);
        when(doctorMapperPort.toDto(updatedDoctor)).thenReturn(doctorDto);

        // when
        DoctorDto result = doctorService.update(id, doctorDto);

        // then
        assertAll(
                () -> assertEquals(updatedDoctor.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(updatedDoctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(updatedDoctor.getLastName(), result.getLastName())
        );
        verify(doctorRepositoryPort, times(1)).findById(id);
        verify(doctorRepositoryPort, times(1)).save(any(Doctor.class));
        verify(doctorMapperPort, times(1)).toDto(updatedDoctor);
        verifyNoMoreInteractions(doctorRepositoryPort, doctorMapperPort);
    }

    @Test
    void addFacilityToDoctor_DateCorrect_ShouldAddFacilityAndReturnDoctorDto() {
        // given
        Long doctorId = 1L;
        Long facilityId = 5L;
        Doctor doctor = Doctor.builder()
                .id(doctorId)
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .facilityIds(new HashSet<>())
                .build();
        Facility facility = Facility.builder()
                .id(facilityId)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        Doctor savedDoctor = Doctor.builder()
                .id(doctorId)
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .facilityIds(Set.of(facilityId))
                .build();
        DoctorDto expectedDto = DoctorDto.builder()
                .id(doctorId)
                .firstName("Ilya")
                .lastName("Osipov")
                .specialization(Specialization.DERMATOLOGY)
                .facilityIds(Set.of(facilityId))
                .build();

        when(doctorRepositoryPort.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepositoryPort.findById(facilityId)).thenReturn(Optional.of(facility));
        when(doctorRepositoryPort.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(doctorMapperPort.toDto(savedDoctor)).thenReturn(expectedDto);

        // when
        DoctorDto result = doctorService.addFacilityToDoctor(doctorId, facilityId);

        // then
        assertAll(
                () -> assertEquals(doctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(1, result.getFacilityIds().size()),
                () -> assertTrue(doctor.getFacilityIds().contains(facilityId))
        );
        verify(doctorRepositoryPort, times(1)).findById(doctorId);
        verify(facilityRepositoryPort, times(1)).findById(facilityId);
        verify(doctorRepositoryPort, times(1)).save(any(Doctor.class));
        verify(doctorMapperPort, times(1)).toDto(savedDoctor);
        verifyNoMoreInteractions(doctorRepositoryPort, facilityRepositoryPort, doctorMapperPort);
    }

    @Test
    void removeFacilityFromDoctor_DataCorrect_ShouldRemoveFacilityAndReturnDoctorDto() {
        // given
        Long doctorId = 1L;
        Long facilityId = 5L;
        Facility facility = Facility.builder()
                .id(facilityId)
                .name("falcons")
                .build();
        Doctor doctor = Doctor.builder()
                .id(doctorId)
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .facilityIds(new HashSet<>(Set.of(facilityId)))
                .build();
        Doctor savedDoctor = Doctor.builder()
                .id(doctorId)
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .facilityIds(new HashSet<>())
                .build();
        DoctorDto expectedDto = DoctorDto.builder()
                .id(doctorId)
                .firstName("Ilya")
                .lastName("Osipov")
                .specialization(Specialization.DERMATOLOGY)
                .facilityIds(new HashSet<>())
                .build();

        when(doctorRepositoryPort.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepositoryPort.findById(facilityId)).thenReturn(Optional.of(facility));
        when(doctorRepositoryPort.save(any(Doctor.class))).thenReturn(savedDoctor);
        when(doctorMapperPort.toDto(savedDoctor)).thenReturn(expectedDto);

        // when
        DoctorDto result = doctorService.removeFacilityFromDoctor(doctorId, facilityId);

        // then
        assertAll(
                () -> assertEquals(doctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(0, result.getFacilityIds().size()),
                () -> assertFalse(doctor.getFacilityIds().contains(facilityId))
        );
        verify(doctorRepositoryPort, times(1)).findById(doctorId);
        verify(facilityRepositoryPort, times(1)).findById(facilityId);
        verify(doctorRepositoryPort, times(1)).save(any(Doctor.class));
        verify(doctorMapperPort, times(1)).toDto(savedDoctor);
        verifyNoMoreInteractions(doctorRepositoryPort, facilityRepositoryPort, doctorMapperPort);
    }

    @Test
    void findById_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        // given
        Long id = 1L;
        when(doctorRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.findById(id));

        assertEquals("Doctor does not exist", result.getMessage());
    }

    @Test
    void create_UserDoesNotExists_ThrowUserDoesNotExistsException() {
        // given
        DoctorCreateCommand command = DoctorCreateCommand.builder()
                .userId(1L)
                .build();
        when(userRepositoryPort.findById(command.getUserId())).thenReturn(Optional.empty());

        // when & then
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> doctorService.create(command));

        assertEquals("User does not exist", result.getMessage());
    }

    @Test
    void create_DoctorAlreadyExists_ThrowDoctorAlreadyExistsException() {
        // given
        User user = User.builder().id(1L).build();
        DoctorCreateCommand command = DoctorCreateCommand.builder().userId(1L).build();
        Doctor existingDoctor = Doctor.builder().id(1L).build();

        when(userRepositoryPort.findById(command.getUserId())).thenReturn(Optional.of(user));
        when(doctorRepositoryPort.findByUserId(command.getUserId())).thenReturn(Optional.of(existingDoctor));

        // when & then
        DoctorAlreadyExistsException result = assertThrows(DoctorAlreadyExistsException.class,
                () -> doctorService.create(command));

        assertEquals("This user is already a doctor", result.getMessage());
    }

    @Test
    void update_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        // given
        Long id = 1L;
        DoctorDto doctorDto = DoctorDto.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("Nikola")
                .lastName("Kovach")
                .build();
        when(doctorRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.update(id, doctorDto));

        assertEquals("Doctor does not exist", result.getMessage());
    }

    @Test
    void addFacilityToDoctor_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        // given
        Long doctorId = 1L;
        Long facilityId = 1L;
        when(doctorRepositoryPort.findById(doctorId)).thenReturn(Optional.empty());

        // when & then
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.addFacilityToDoctor(doctorId, facilityId));

        assertEquals("Doctor does not exist", result.getMessage());
    }

    @Test
    void addFacilityToDoctor_FacilityDoesNotExists_ThrowFacilityDoesNotExistsException() {
        // given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = Doctor.builder().id(doctorId).build();
        when(doctorRepositoryPort.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepositoryPort.findById(facilityId)).thenReturn(Optional.empty());

        // when & then
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> doctorService.addFacilityToDoctor(doctorId, facilityId));

        assertEquals("Facility does not exist", result.getMessage());
    }

    @Test
    void removeFacilityFromDoctor_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        // given
        Long doctorId = 1L;
        Long facilityId = 1L;
        when(doctorRepositoryPort.findById(doctorId)).thenReturn(Optional.empty());

        // when & then
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.removeFacilityFromDoctor(doctorId, facilityId));

        assertEquals("Doctor does not exist", result.getMessage());
    }

    @Test
    void removeFacilityFromDoctor_FacilityDoesNotExists_ThrowFacilityDoesNotExistsException() {
        // given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = Doctor.builder().id(doctorId).build();
        when(doctorRepositoryPort.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepositoryPort.findById(facilityId)).thenReturn(Optional.empty());

        // when & then
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> doctorService.removeFacilityFromDoctor(doctorId, facilityId));

        assertEquals("Facility does not exist", result.getMessage());
    }

    @Test
    void delete_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        // given
        Long id = 1L;
        when(doctorRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.delete(id));

        assertEquals("Doctor does not exist", result.getMessage());
    }

    @Test
    void delete_DataCorrect_DeleteDoctor() {
        // given
        Long id = 1L;
        Doctor doctor = Doctor.builder().id(id).build();
        when(doctorRepositoryPort.findById(id)).thenReturn(Optional.of(doctor));

        // when
        doctorService.delete(id);

        // then
        verify(doctorRepositoryPort, times(1)).findById(id);
        verify(doctorRepositoryPort, times(1)).deleteById(id);
        verifyNoMoreInteractions(doctorRepositoryPort);
    }
}