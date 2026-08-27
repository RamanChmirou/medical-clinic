package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.DoctorAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.DoctorDoesNotExistsException;
import com.kanapa4.medical_clinic.exception.FacilityDoesNotExistsException;
import com.kanapa4.medical_clinic.exception.UserDoesNotExistsException;
import com.kanapa4.medical_clinic.mapper.DoctorMapper;
import com.kanapa4.medical_clinic.mapper.DoctorMapperImpl;
import com.kanapa4.medical_clinic.mapper.FacilityMapper;
import com.kanapa4.medical_clinic.model.Role;
import com.kanapa4.medical_clinic.model.Specialization;
import com.kanapa4.medical_clinic.model.dto.DoctorCreateCommand;
import com.kanapa4.medical_clinic.model.dto.DoctorDto;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import com.kanapa4.medical_clinic.model.entity.Doctor;
import com.kanapa4.medical_clinic.model.entity.Facility;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.DoctorRepository;
import com.kanapa4.medical_clinic.repository.FacilityRepository;
import com.kanapa4.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;
    private FacilityRepository facilityRepository;
    private DoctorMapper doctorMapper;
    private DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.doctorRepository = mock(DoctorRepository.class);
        this.userRepository = mock(UserRepository.class);
        this.facilityRepository = mock(FacilityRepository.class);
        this.doctorMapper = new DoctorMapperImpl(Mappers.getMapper(FacilityMapper.class));
        this.doctorService = new DoctorService(doctorRepository, userRepository, facilityRepository, doctorMapper);
    }

    @Test
    void getPaginatedDoctors_DataCorrect_ReturnPaginatedDoctorDtos() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        User user = User.builder()
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        Doctor doctor = Doctor.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .user(user)
                .build();
        Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor), pageable, 1);
        when(doctorRepository.findAll(pageable)).thenReturn(doctorPage);
        //when
        Page<DoctorDto> result = doctorService.getPaginatedDoctors(0, 10, "id", null);
        //then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(doctor.getFirstName(), result.getContent().getFirst().getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getContent().getFirst().getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getContent().getFirst().getSpecialization())
        );
        verify(doctorRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void getPaginatedDoctorsWithSpecialization_DataCorrect_ReturnPaginatedDoctorDtos() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        User user = User.builder()
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        Doctor doctor = Doctor.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .user(user)
                .build();
        Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor), pageable, 1);
        when(doctorRepository.findBySpecialization(Specialization.CARDIOLOGY, pageable)).thenReturn(doctorPage);
        //when
        Page<DoctorDto> result = doctorService.getPaginatedDoctors(0, 10, "id", Specialization.CARDIOLOGY);
        //then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(doctor.getFirstName(), result.getContent().getFirst().getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getContent().getFirst().getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getContent().getFirst().getSpecialization())
        );
        verify(doctorRepository, times(1)).findBySpecialization(Specialization.CARDIOLOGY, pageable);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findById_DataCorrect_ReturnFoundDoctorDto() {
        //given
        User user = User.builder()
                .id(1L)
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        Doctor doctor = Doctor.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .user(user)
                .build();
        Long id = 1L;
        when(doctorRepository.findById(id)).thenReturn(Optional.of(doctor));
        //when
        DoctorDto result = doctorService.findById(id);
        //then
        assertAll(
                () -> assertEquals(doctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getSpecialization())
        );
        verify(doctorRepository, times(1)).findById(id);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void create_DataCorrect_ReturnCreatedDoctorDto() {
        //given
        User user = User.builder()
                .id(1L)
                .email("email@com")
                .role(Role.DOCTOR)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        DoctorCreateCommand doctorCreateCommand = DoctorCreateCommand.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .userId(1L)
                .build();
        Doctor savedDoctor = Doctor.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("ala")
                .lastName("pala")
                .user(user)
                .build();
        when(doctorRepository.findByUserId(doctorCreateCommand.getUserId())).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        //when
        DoctorDto result = doctorService.create(doctorCreateCommand);
        //then
        assertAll(
                () -> assertEquals(doctorCreateCommand.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(doctorCreateCommand.getFirstName(), result.getFirstName()),
                () -> assertEquals(doctorCreateCommand.getLastName(), result.getLastName())
        );
        verify(userRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findByUserId(doctorCreateCommand.getUserId());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verifyNoMoreInteractions(doctorRepository, userRepository);
    }

    @Test
    void update_DataCorrect_ReturnUpdatedDoctorDto() {
        //given
        Long id = 1L;
        DoctorDto doctorDto = DoctorDto.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("Nikola")
                .lastName("Kovach")
                .build();
        Doctor doctorToUpdate = Doctor.builder()
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .build();
        Doctor updatedDoctor = Doctor.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("Nikola")
                .lastName("Kovach")
                .build();
        when(doctorRepository.findById(id)).thenReturn(Optional.of(doctorToUpdate));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);
        //when
        DoctorDto result = doctorService.update(id, doctorDto);
        //then
        assertAll(
                () -> assertEquals(updatedDoctor.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(updatedDoctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(updatedDoctor.getLastName(), result.getLastName())
        );
        verify(doctorRepository, times(1)).findById(id);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void addFacilityToDoctor_DateCorrect_ShouldAddFacilityAndReturnDoctorDto() {
        //given
        Long doctorId = 1L;
        Long facilityId = 5L;
        Doctor doctor = Doctor.builder()
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .build();
        Facility facility = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        Doctor savedDoctor = Doctor.builder()
                .id(1L)
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .facilities(Set.of(facility))
                .build();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        //when
        DoctorDto result = doctorService.addFacilityToDoctor(doctorId, facilityId);
        //then
        assertAll(
                () -> assertEquals(doctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(1, result.getFacilities().size()),
                () -> {
                    FacilityDto facilityDto = result.getFacilities().iterator().next();
                    assertAll(
                            () -> assertEquals(facility.getName(), facilityDto.getName()),
                            () -> assertEquals(facility.getCity(), facilityDto.getCity()),
                            () -> assertEquals(facility.getZipCode(), facilityDto.getZipCode()),
                            () -> assertEquals(facility.getStreet(), facilityDto.getStreet()),
                            () -> assertEquals(facility.getBuildingNumber(), facilityDto.getBuildingNumber())
                    );
                }
        );
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(facilityRepository, times(1)).findById(facilityId);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verifyNoMoreInteractions(doctorRepository, facilityRepository);
    }

    @Test
    void removeFacilityFromDoctor_DataCorrect_ShouldRemoveFacilityAndReturnDoctorDto() {
        //given
        Long doctorId = 1L;
        Long facilityId = 5L;
        Facility facility = Facility.builder()
                .id(5L)
                .name("falcons")
                .city("Cologne")
                .zipCode("228-00")
                .street("zonik")
                .buildingNumber("12A")
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .facilities(new HashSet<>(Set.of(facility)))
                .build();
        Doctor savedDoctor = Doctor.builder()
                .specialization(Specialization.DERMATOLOGY)
                .firstName("Ilya")
                .lastName("Osipov")
                .build();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);
        //when
        DoctorDto result = doctorService.removeFacilityFromDoctor(doctorId, facilityId);
        //then
        assertAll(
                () -> assertEquals(doctor.getFirstName(), result.getFirstName()),
                () -> assertEquals(doctor.getLastName(), result.getLastName()),
                () -> assertEquals(doctor.getSpecialization(), result.getSpecialization()),
                () -> assertEquals(0, result.getFacilities().size())
        );
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(facilityRepository, times(1)).findById(facilityId);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
        verifyNoMoreInteractions(doctorRepository, facilityRepository);
    }

    @Test
    void findById_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        //given
        Long id = 1L;
        when(doctorRepository.findById(id)).thenReturn(Optional.empty());
        //when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.findById(id));
        //then
        assertAll(
                () -> assertEquals("Doctor does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void create_UserDoesNotExists_ThrowUserDoesNotExistsException() {
        //given
        DoctorCreateCommand command = DoctorCreateCommand.builder()
                .userId(1L)
                .build();
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.empty());
        //when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> doctorService.create(command));
        //then
        assertAll(
                () -> assertEquals("User does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void create_DoctorAlreadyExists_ThrowDoctorAlreadyExistsException() {
        //given
        User user = User.builder()
                .id(1L)
                .build();
        DoctorCreateCommand command = DoctorCreateCommand.builder()
                .userId(1L)
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .build();
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(command.getUserId())).thenReturn(Optional.of(doctor));
        //when
        DoctorAlreadyExistsException result = assertThrows(DoctorAlreadyExistsException.class,
                () -> doctorService.create(command));
        //then
        assertAll(
                () -> assertEquals("This user is already a doctor", result.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, result.getHttpStatus())
        );
    }

    @Test
    void update_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        //given
        Long id = 1L;
        DoctorDto doctorDto = DoctorDto.builder()
                .specialization(Specialization.CARDIOLOGY)
                .firstName("Nikola")
                .lastName("Kovach")
                .build();
        when(doctorRepository.findById(id)).thenReturn(Optional.empty());
        //when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.update(id, doctorDto));
        //then
        assertAll(
                () -> assertEquals("Doctor does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void addFacilityToDoctor_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        //given
        Long doctorId = 1L;
        Long facilityId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.addFacilityToDoctor(doctorId, facilityId));
        //then
        assertAll(
                () -> assertEquals("Doctor does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void addFacilityToDoctor_FacilityDoesNotExists_ThrowFacilityDoesNotExistsException() {
        //given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = Doctor.builder()
                .id(1L)
                .build();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());
        //when
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> doctorService.addFacilityToDoctor(doctorId, facilityId));
        //then
        assertAll(
                () -> assertEquals("Facility does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void removeFacilityFromDoctor_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        //given
        Long doctorId = 1L;
        Long facilityId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        //when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.removeFacilityFromDoctor(doctorId, facilityId));
        //then
        assertAll(
                () -> assertEquals("Doctor does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void removeFacilityFromDoctor_FacilityDoesNotExists_ThrowFacilityDoesNotExistsException() {
        //given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = Doctor.builder()
                .id(1L)
                .build();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());
        //when
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> doctorService.removeFacilityFromDoctor(doctorId, facilityId));
        //then
        assertAll(
                () -> assertEquals("Facility does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void delete_DoctorDoesNotExists_ThrowDoctorDoesNotExistsException() {
        //given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> doctorService.delete(1L));
        //then
        assertAll(
                () -> assertEquals("Doctor does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void delete_DataCorrect_DeleteDoctor() {
        //given
        Doctor doctor = Doctor.builder().build();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        //when
        doctorService.delete(1L);
        //then
        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(doctorRepository);
    }
}
