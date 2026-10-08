package com.kanapa4.medical_clinic;

import com.kanapa4.medical_clinic.domain.PatientService;
import com.kanapa4.medical_clinic.domain.port.PatientMapperPort;
import com.kanapa4.medical_clinic.domain.port.PatientRepositoryPort;
import com.kanapa4.medical_clinic.domain.port.UserRepositoryPort;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.PatientAlreadyExistsException;
import com.kanapa4.medical_clinic.model.exception.PatientDoesNotExistsException;
import com.kanapa4.medical_clinic.model.exception.UserDoesNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PatientServiceTest {

    private PatientRepositoryPort patientRepositoryPort;
    private PatientMapperPort patientMapperPort;
    private UserRepositoryPort userRepositoryPort;
    private PatientService patientService;

    @BeforeEach
    void setup() {
        this.patientRepositoryPort = mock(PatientRepositoryPort.class);
        this.patientMapperPort = mock(PatientMapperPort.class);
        this.userRepositoryPort = mock(UserRepositoryPort.class);
        this.patientService = new PatientService(patientRepositoryPort, patientMapperPort, userRepositoryPort);
    }

    @Test
    void getPaginatedPatients_DataCorrect_ReturnPaginatedPatientDtos() {
        // given
        Pageable pageable = new Pageable(0, 10);
        Patient patient = Patient.builder()
                .idCardNo("10")
                .firstName("ala")
                .lastName("pala")
                .build();
        Page<Patient> patientPage = Page.<Patient>builder()
                .content(List.of(patient))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
        Page<PatientDto> expectedDtoPage = Page.<PatientDto>builder()
                .content(List.of(PatientDto.builder().firstName("ala").lastName("pala").build()))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(patientRepositoryPort.findAll(pageable)).thenReturn(patientPage);
        when(patientMapperPort.toPageDto(patientPage)).thenReturn(expectedDtoPage);

        // when
        Page<PatientDto> result = patientService.getPaginatedPatients(pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals("ala", result.getContent().getFirst().getFirstName()),
                () -> assertEquals("pala", result.getContent().getFirst().getLastName())
        );
        verify(patientRepositoryPort, times(1)).findAll(pageable);
        verify(patientMapperPort, times(1)).toPageDto(patientPage);
        verifyNoMoreInteractions(patientRepositoryPort, patientMapperPort);
    }

    @Test
    void create_DataCorrect_ReturnCreatedPatientDto() {
        // given
        User user = User.builder()
                .id(1L)
                .email("email@com")
                .role(Role.PATIENT)
                .build();
        PatientCreateCommand command = PatientCreateCommand.builder()
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .birthday(LocalDate.of(2006, 5, 19))
                .phoneNumber("123456789")
                .userId(1L)
                .build();
        Patient savedPatient = Patient.builder()
                .id(1L)
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .phoneNumber("123456789")
                .userId(user.getId())
                .build();
        PatientDto expectedDto = PatientDto.builder()
                .firstName("first")
                .lastName("last")
                .phoneNumber("123456789")
                .build();

        when(patientRepositoryPort.findByIdCardNo(command.getIdCardNo())).thenReturn(Optional.empty());
        when(userRepositoryPort.findById(user.getId())).thenReturn(Optional.of(user));
        when(patientRepositoryPort.save(any(Patient.class))).thenReturn(savedPatient);
        when(patientMapperPort.toDto(savedPatient)).thenReturn(expectedDto);

        // when
        PatientDto result = patientService.create(command);

        // then
        assertAll(
                () -> assertEquals(command.getFirstName(), result.getFirstName()),
                () -> assertEquals(command.getLastName(), result.getLastName()),
                () -> assertEquals(command.getPhoneNumber(), result.getPhoneNumber())
        );
        verify(patientRepositoryPort, times(1)).findByIdCardNo(command.getIdCardNo());
        verify(userRepositoryPort, times(1)).findById(user.getId());
        verify(patientRepositoryPort, times(1)).save(any(Patient.class));
        verify(patientMapperPort, times(1)).toDto(savedPatient);
        verifyNoMoreInteractions(patientRepositoryPort, userRepositoryPort, patientMapperPort);
    }

    @Test
    void findById_DataCorrect_ReturnFoundPatientDto() {
        // given
        Long id = 1L;
        Patient patient = Patient.builder()
                .id(id)
                .firstName("first")
                .lastName("last")
                .phoneNumber("123456789")
                .build();
        PatientDto expectedDto = PatientDto.builder()
                .firstName("first")
                .lastName("last")
                .phoneNumber("123456789")
                .build();

        when(patientRepositoryPort.findById(id)).thenReturn(Optional.of(patient));
        when(patientMapperPort.toDto(patient)).thenReturn(expectedDto);

        // when
        PatientDto result = patientService.findById(id);

        // then
        assertAll(
                () -> assertEquals(patient.getFirstName(), result.getFirstName()),
                () -> assertEquals(patient.getLastName(), result.getLastName()),
                () -> assertEquals(patient.getPhoneNumber(), result.getPhoneNumber())
        );
        verify(patientRepositoryPort, times(1)).findById(id);
        verify(patientMapperPort, times(1)).toDto(patient);
        verifyNoMoreInteractions(patientRepositoryPort, patientMapperPort);
    }

    @Test
    void createPatientForUser_DataCorrect_ReturnCreatedPatientDto() {
        // given
        String userEmail = "email@com";
        User user = User.builder()
                .id(1L)
                .email(userEmail)
                .role(Role.PATIENT)
                .build();
        PatientCreateCommand command = PatientCreateCommand.builder()
                .firstName("first")
                .lastName("last")
                .idCardNo("12345")
                .birthday(LocalDate.of(2006, 5, 19))
                .phoneNumber("123456789")
                .build();
        Patient savedPatient = Patient.builder()
                .id(1L)
                .firstName("first")
                .lastName("last")
                .phoneNumber("123456789")
                .userId(user.getId())
                .build();
        PatientDto expectedDto = PatientDto.builder()
                .firstName("first")
                .lastName("last")
                .phoneNumber("123456789")
                .build();

        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(patientRepositoryPort.save(any(Patient.class))).thenReturn(savedPatient);
        when(patientMapperPort.toDto(savedPatient)).thenReturn(expectedDto);

        // when
        PatientDto result = patientService.createPatientForUser(userEmail, command);

        // then
        assertAll(
                () -> assertEquals("first", result.getFirstName()),
                () -> assertEquals("last", result.getLastName()),
                () -> assertEquals("123456789", result.getPhoneNumber())
        );
        verify(userRepositoryPort, times(1)).findByEmail(userEmail);
        verify(patientRepositoryPort, times(1)).save(any(Patient.class));
        verify(patientMapperPort, times(1)).toDto(savedPatient);
        verifyNoMoreInteractions(patientRepositoryPort, userRepositoryPort, patientMapperPort);
    }

    @Test
    void update_DataCorrect_ReturnUpdatedPatientDto() {
        // given
        Long id = 1L;
        Patient existing = Patient.builder()
                .id(id)
                .firstName("first")
                .lastName("last")
                .phoneNumber("123456789")
                .build();
        PatientDto updateDto = PatientDto.builder()
                .firstName("newFirst")
                .lastName("newLast")
                .phoneNumber("222222222")
                .build();
        Patient savedPatient = Patient.builder()
                .id(id)
                .firstName("newFirst")
                .lastName("newLast")
                .phoneNumber("222222222")
                .build();

        when(patientRepositoryPort.findById(id)).thenReturn(Optional.of(existing));
        when(patientRepositoryPort.save(existing)).thenReturn(savedPatient);
        when(patientMapperPort.toDto(savedPatient)).thenReturn(updateDto);

        // when
        PatientDto result = patientService.update(id, updateDto);

        // then
        assertAll(
                () -> assertEquals(updateDto.getFirstName(), result.getFirstName()),
                () -> assertEquals(updateDto.getLastName(), result.getLastName()),
                () -> assertEquals(updateDto.getPhoneNumber(), result.getPhoneNumber())
        );
        verify(patientRepositoryPort, times(1)).findById(id);
        verify(patientRepositoryPort, times(1)).save(existing);
        verify(patientMapperPort, times(1)).toDto(savedPatient);
        verifyNoMoreInteractions(patientRepositoryPort, patientMapperPort);
    }

    @Test
    void findById_PatientDoesNotExists_ThrowPatientDoesNotExistsException() {
        // given
        Long id = 1L;
        when(patientRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // when & then
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> patientService.findById(id));

        assertEquals("Patient does not exist", result.getMessage());
    }

    @Test
    void createPatientForUser_UserNotFound_ThrowUserDoesNotExistsException() {
        // given
        String userEmail = "email@com";
        PatientCreateCommand command = PatientCreateCommand.builder()
                .firstName("firstName")
                .lastName("lastName")
                .build();
        when(userRepositoryPort.findByEmail(userEmail)).thenReturn(Optional.empty());

        // when & then
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> patientService.createPatientForUser(userEmail, command));

        assertEquals("User not found", result.getMessage());
    }

    @Test
    void create_PatientAlreadyExists_ThrowPatientAlreadyExistsException() {
        // given
        PatientCreateCommand command = PatientCreateCommand.builder()
                .idCardNo("12345")
                .build();
        when(patientRepositoryPort.findByIdCardNo(command.getIdCardNo())).thenReturn(Optional.of(Patient.builder().build()));

        // when & then
        PatientAlreadyExistsException result = assertThrows(PatientAlreadyExistsException.class,
                () -> patientService.create(command));

        assertEquals("Patient already exists", result.getMessage());
    }

    @Test
    void create_UserDoesNotExist_ThrowUserDoesNotExistsException() {
        // given
        PatientCreateCommand command = PatientCreateCommand.builder()
                .idCardNo("12345")
                .userId(1L)
                .build();
        when(patientRepositoryPort.findByIdCardNo(command.getIdCardNo())).thenReturn(Optional.empty());
        when(userRepositoryPort.findById(command.getUserId())).thenReturn(Optional.empty());

        // when & then
        UserDoesNotExistsException result = assertThrows(UserDoesNotExistsException.class,
                () -> patientService.create(command));

        assertEquals("User does not exist", result.getMessage());
    }

    @Test
    void update_PatientDoesNotExist_ThrowPatientDoesNotExistsException() {
        // given
        PatientDto dto = PatientDto.builder().build();
        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // when & then
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> patientService.update(1L, dto));

        assertEquals("Patient does not exist", result.getMessage());
    }

    @Test
    void delete_PatientDoesNotExists_ThrowPatientDoesNotExistsException() {
        // given
        when(patientRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // when & then
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> patientService.delete(1L));

        assertEquals("Patient does not exist", result.getMessage());
    }

    @Test
    void delete_DataCorrect_DeletePatient() {
        // given
        Long id = 1L;
        Patient patient = Patient.builder().id(id).build();
        when(patientRepositoryPort.findById(id)).thenReturn(Optional.of(patient));

        // when
        patientService.delete(id);

        // then
        verify(patientRepositoryPort, times(1)).findById(id);
        verify(patientRepositoryPort, times(1)).deleteById(id);
        verifyNoMoreInteractions(patientRepositoryPort);
    }
}