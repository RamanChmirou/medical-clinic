package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.PatientAlreadyExistsException;
import com.kanapa4.medical_clinic.exception.PatientDoesNotExistsException;
import com.kanapa4.medical_clinic.exception.UserDoesNotExistsException;
import com.kanapa4.medical_clinic.mapper.PatientMapper;
import com.kanapa4.medical_clinic.model.Role;
import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
import com.kanapa4.medical_clinic.model.dto.PatientDto;
import com.kanapa4.medical_clinic.model.entity.Patient;
import com.kanapa4.medical_clinic.model.entity.User;
import com.kanapa4.medical_clinic.repository.PatientRepository;
import com.kanapa4.medical_clinic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PatientServiceTest {
    private PatientRepository patientRepository;
    private PatientMapper patientMapper;
    private UserRepository userRepository;
    private PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientRepository = mock(PatientRepository.class);
        this.patientMapper = getMapper(PatientMapper.class);
        this.userRepository = mock(UserRepository.class);
        this.patientService = new PatientService(patientRepository, patientMapper, userRepository);
    }

    @Test
    void getPaginatedPatients_DataCorrect_ReturnPaginatedPatientDtos() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("idCardNo").ascending());
        User user = User.builder()
                .email("email@com")
                .role(Role.PATIENT)
                .build();
        Patient patient = Patient.builder()
                .idCardNo("10")
                .firstName("ala")
                .lastName("pala")
                .user(user)
                .build();
        Page<Patient> patientPage = new PageImpl<>(List.of(patient), pageable, 1);
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        //when
        Page<PatientDto> result = patientService.getPaginatedPatients(0, 10, "idCardNo");
        //then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals("ala", result.getContent().getFirst().getFirstName()),
                () -> assertEquals("pala", result.getContent().getFirst().getLastName())
        );
        verify(patientRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void create_DataCorrect_ReturnCreatedPatientDto() {
        //given
        User user = User.builder()
                .id(1L)
                .email("email@com")
                .role(Role.PATIENT)
                .build();
        PatientCreateCommand patientCreateCommand = PatientCreateCommand.builder()
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .birthday(LocalDate.of(2006, 5, 19))
                .phoneNumber("123456789")
                .userId(1L)
                .build();
        Patient savedPatient = Patient.builder()
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .birthday(LocalDate.of(2006, 5, 19))
                .phoneNumber("123456789")
                .user(user)
                .build();
        when(patientRepository.findByIdCardNo(patientCreateCommand.getIdCardNo())).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        //when
        PatientDto result = patientService.create(patientCreateCommand);
        //then
        assertAll(
                () -> assertEquals(patientCreateCommand.getFirstName(), result.getFirstName()),
                () -> assertEquals(patientCreateCommand.getLastName(), result.getLastName()),
                () -> assertEquals(patientCreateCommand.getPhoneNumber(), result.getPhoneNumber())
        );
        verify(patientRepository, times(1)).findByIdCardNo(patientCreateCommand.getIdCardNo());
        verify(userRepository, times(1)).findById(user.getId());
        verify(patientRepository, times(1)).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository, userRepository);
    }

    @Test
    void findById_DataCorrect_ReturnFoundPatientDto() {
        //given
        User user = User.builder()
                .id(1L)
                .email("email@com")
                .role(Role.PATIENT)
                .build();
        Patient patient = Patient.builder()
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .birthday(LocalDate.of(2006, 5, 19))
                .phoneNumber("123456789")
                .user(user)
                .build();
        Long id = 1L;
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        //when
        PatientDto result = patientService.findById(id);
        //then
        assertAll(
                () -> assertEquals(patient.getFirstName(), result.getFirstName()),
                () -> assertEquals(patient.getLastName(), result.getLastName()),
                () -> assertEquals(patient.getPhoneNumber(), result.getPhoneNumber())
        );
        verify(patientRepository, times(1)).findById(id);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void createPatientForUser_DataCorrect_ReturnCreatedPatientDto() {
        //given
        User user = User.builder()
                .id(1L)
                .email("email@com")
                .role(Role.PATIENT)
                .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        PatientCreateCommand patientCreateCommand = PatientCreateCommand.builder()
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .birthday(LocalDate.of(2006, 5, 19))
                .phoneNumber("123456789")
                .userId(1L)
                .build();
        Patient savedPatient = Patient.builder()
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .birthday(LocalDate.of(2006, 5, 19))
                .phoneNumber("123456789")
                .user(user)
                .build();
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        //when
        PatientDto result = patientService.createPatientForUser(user.getEmail(), patientCreateCommand);
        //then
        assertAll(
                () -> assertEquals("first", result.getFirstName()),
                () -> assertEquals("last", result.getLastName()),
                () -> assertEquals("123456789", result.getPhoneNumber())
        );
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(patientRepository, times(1)).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository, userRepository);
    }

    @Test
    void update_DataCorrect_ReturnUpdatedPatientDto() {
        //given
        Long id = 1L;
        Patient patientForUpdate = Patient.builder()
                .id(1L)
                .idCardNo("115511")
                .firstName("first")
                .lastName("last")
                .phoneNumber("123456789")
                .birthday(LocalDate.of(2006, 5, 19))
                .build();
        PatientDto patientDto = PatientDto.builder()
                .firstName("newFirst")
                .lastName("newLast")
                .phoneNumber("222222222")
                .build();
        Patient updatedPatient = Patient.builder()
                .id(1L)
                .idCardNo("115511")
                .firstName("newFirst")
                .lastName("newLast")
                .phoneNumber("222222222")
                .birthday(LocalDate.of(2006, 5, 19))
                .build();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patientForUpdate));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);
        //when
        PatientDto result = patientService.update(id, patientDto);
        //then
        assertAll(
                () -> assertEquals(patientDto.getFirstName(), result.getFirstName()),
                () -> assertEquals(patientDto.getLastName(), result.getLastName()),
                () -> assertEquals(patientDto.getPhoneNumber(), result.getPhoneNumber())
        );
        verify(patientRepository, times(1)).findById(id);
        verify(patientRepository, times(1)).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void findById_PatientDoesNotExists_ThrowPatientDoesNotExistsException() {
        //given
        Long id = 1L;
        when(patientRepository.findById(id)).thenReturn(Optional.empty());
        //when
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> patientService.findById(id));
        //then
        assertAll(
                () -> assertEquals("Patient does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void createPatientForUser_UserNotFound_ThrowUserDoesNotExistsException() {
        //given
        String userEmail = "email";
        PatientCreateCommand patientCreateCommand = PatientCreateCommand.builder()
                .firstName("firstName")
                .lastName("lastName")
                .build();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        //when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> patientService.createPatientForUser(userEmail, patientCreateCommand));
        //then
        assertAll(
                () -> assertEquals("User not found", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void create_PatientAlreadyExists_ThrowPatientAlreadyExistsException() {
        //given
        PatientCreateCommand command = PatientCreateCommand.builder()
                .idCardNo("12345")
                .build();
        Patient existingPatient = Patient.builder().build();
        when(patientRepository.findByIdCardNo(command.getIdCardNo())).thenReturn(Optional.of(existingPatient));
        //when
        PatientAlreadyExistsException result = assertThrows(PatientAlreadyExistsException.class,
                () -> patientService.create(command));
        //then
        assertAll(
                () -> assertEquals("Patient already exists", result.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, result.getHttpStatus())
        );
    }

    @Test
    void create_UserDoesNotExist_ThrowUserDoesNotExistsException() {
        //given
        PatientCreateCommand command = PatientCreateCommand.builder()
                .idCardNo("12345")
                .userId(1L)
                .build();
        when(patientRepository.findByIdCardNo(command.getIdCardNo())).thenReturn(Optional.empty());
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.empty());
        //when
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> patientService.create(command));
        //then
        assertAll(
                () -> assertEquals("User does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void update_PatientDoesNotExist_ThrowPatientDoesNotExistsException() {
        //given
        PatientDto dto = PatientDto.builder().build();
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> patientService.update(1L, dto));
        //then
        assertAll(
                () -> assertEquals("Patient does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void delete_PatientDoesNotExists_ThrowPatientDoesNotExistsException() {
        //given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> patientService.delete(1L));
        //then
        assertAll(
                () -> assertEquals("Patient does not exist", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void delete_DataCorrect_DeletePatient() {
        //given
        Patient patient = Patient.builder().build();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        //when
        patientService.delete(1L);
        //then
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(patientRepository);
    }
}
