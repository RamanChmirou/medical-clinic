package com.kanapa4.medical_clinic.service;

import com.kanapa4.medical_clinic.exception.*;
import com.kanapa4.medical_clinic.mapper.VisitMapper;
import com.kanapa4.medical_clinic.model.dto.VisitCreateCommand;
import com.kanapa4.medical_clinic.model.dto.VisitDto;
import com.kanapa4.medical_clinic.model.entity.Doctor;
import com.kanapa4.medical_clinic.model.entity.Facility;
import com.kanapa4.medical_clinic.model.entity.Patient;
import com.kanapa4.medical_clinic.model.entity.Visit;
import com.kanapa4.medical_clinic.repository.DoctorRepository;
import com.kanapa4.medical_clinic.repository.FacilityRepository;
import com.kanapa4.medical_clinic.repository.PatientRepository;
import com.kanapa4.medical_clinic.repository.VisitRepository;
import com.kanapa4.medical_clinic.validator.VisitValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VisitServiceTest {
    private VisitRepository visitRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private FacilityRepository facilityRepository;
    private VisitValidator visitValidator;
    private VisitMapper visitMapper;
    private VisitService visitService;

    @BeforeEach
    void setup() {
        this.visitRepository = mock(VisitRepository.class);
        this.doctorRepository = mock(DoctorRepository.class);
        this.patientRepository = mock(PatientRepository.class);
        this.facilityRepository = mock(FacilityRepository.class);
        this.visitValidator = mock(VisitValidator.class);
        this.visitMapper = getMapper(VisitMapper.class);

        this.visitService = new VisitService(
                visitRepository,
                doctorRepository,
                patientRepository,
                visitMapper,
                facilityRepository,
                visitValidator
        );
    }

    @Test
    void getPaginatedVisits_DataCorrect_ReturnPaginatedVisitDtos() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dateTime").ascending());
        Visit visit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        Page<Visit> visitPage = new PageImpl<>(List.of(visit), pageable, 1);
        when(visitRepository.findAll(pageable)).thenReturn(visitPage);
        //when
        Page<VisitDto> result = visitService.getPaginatedVisits(0, 10, "dateTime");
        //then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(visit.getDateTime(), result.getContent().getFirst().getDateTime()),
                () -> assertEquals(visit.getDurationInMinutes(), result.getContent().getFirst().getDurationInMinutes())
        );
        verify(visitRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void createVisitSlot_DataCorrect_ReturnCreatedVisitDto() {
        //given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .doctorId(1L)
                .facilityId(2L)
                .build();
        Doctor doctor = Doctor.builder().id(1L).build();
        Facility facility = Facility.builder().id(2L).build();
        Visit savedVisit = Visit.builder()
                .id(10L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .doctor(doctor)
                .facility(facility)
                .build();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(2L)).thenReturn(Optional.of(facility));
        when(visitRepository.save(any(Visit.class))).thenReturn(savedVisit);
        //when
        VisitDto result = visitService.createVisitSlot(command);
        //then
        assertAll(
                () -> assertEquals(command.getDateTime(), result.getDateTime()),
                () -> assertEquals(command.getDurationInMinutes(), result.getDurationInMinutes())
        );
        verify(doctorRepository, times(1)).findById(1L);
        verify(facilityRepository, times(1)).findById(2L);
        verify(visitRepository, times(1)).save(any(Visit.class));
        verifyNoMoreInteractions(visitRepository, doctorRepository, facilityRepository);
    }

    @Test
    void updateVisit_DataCorrect_ReturnUpdatedVisitDto() {
        //given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .dateTime(LocalDateTime.of(2026, 7, 8, 14, 0))
                .durationInMinutes(30)
                .doctorId(20L)
                .patientId(5L)
                .build();
        Doctor currentDoctor = Doctor.builder().id(10L).build();
        Doctor newDoctor = Doctor.builder().id(20L).build();
        Patient patient = Patient.builder().id(5L).build();
        Visit existingVisit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .doctor(currentDoctor)
                .build();
        Visit updatedVisit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 8, 14, 0))
                .durationInMinutes(30)
                .doctor(newDoctor)
                .patient(patient)
                .build();
        when(visitRepository.findById(1L)).thenReturn(Optional.of(existingVisit));
        when(doctorRepository.findById(20L)).thenReturn(Optional.of(newDoctor));
        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));
        when(visitRepository.save(any(Visit.class))).thenReturn(updatedVisit);
        //when
        VisitDto result = visitService.updateVisit(1L, command);
        //then
        assertAll(
                () -> assertEquals(command.getDateTime(), result.getDateTime()),
                () -> assertEquals(command.getDurationInMinutes(), result.getDurationInMinutes())
        );
        verify(visitRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(20L);
        verify(patientRepository, times(1)).findById(5L);
        verify(visitRepository, times(1)).save(any(Visit.class));
        verifyNoMoreInteractions(visitRepository, doctorRepository, patientRepository);
    }

    @Test
    void bookVisit_DataCorrect_ReturnBookedVisitDto() {
        //given
        Patient patient = Patient.builder().id(2L).build();
        Visit openVisit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().plusDays(1))
                .patient(null)
                .build();
        when(visitRepository.findById(1L)).thenReturn(Optional.of(openVisit));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        //when
        VisitDto result = visitService.bookVisit(1L, 2L);
        //then
        assertAll(
                () -> assertEquals(patient.getId(), result.getPatientId())
        );
        verify(visitRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(visitRepository, patientRepository);
    }

    @Test
    void getPatientVisits_DataCorrect_ReturnListOfVisitDtos() {
        //given
        Visit visit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(visitRepository.findAllByPatientId(1L)).thenReturn(List.of(visit));
        //when
        List<VisitDto> result = visitService.getPatientVisits(1L);
        //then
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(visit.getDateTime(), result.getFirst().getDateTime()),
                () -> assertEquals(visit.getDurationInMinutes(), result.getFirst().getDurationInMinutes())
        );
        verify(patientRepository, times(1)).existsById(1L);
        verify(visitRepository, times(1)).findAllByPatientId(1L);
        verifyNoMoreInteractions(visitRepository, patientRepository);
    }

    @Test
    void createVisitSlot_DoctorDoesNotExist_ThrowDoctorDoesNotExistsException() {
        //given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(1L)
                .build();
        when(doctorRepository.findById(command.getDoctorId())).thenReturn(Optional.empty());
        //when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> visitService.createVisitSlot(command));
        //then
        assertAll(
                () -> assertEquals("Doctor does not exist.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void createVisitSlot_FacilityDoesNotExist_ThrowFacilityDoesNotExistsException() {
        //given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(1L)
                .facilityId(2L)
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .build();
        when(doctorRepository.findById(command.getDoctorId())).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(command.getFacilityId())).thenReturn(Optional.empty());
        //when
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> visitService.createVisitSlot(command));
        //then
        assertAll(
                () -> assertEquals("Facility does not exist.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void updateVisit_VisitDoesNotExist_ThrowVisitDoesNotExistsException() {
        //given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .build();
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        VisitDoesNotExistsException result = assertThrows(VisitDoesNotExistsException.class,
                () -> visitService.updateVisit(1L, command));
        //then
        assertAll(
                () -> assertEquals("Visit does not exist.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void updateVisit_DoctorDoesNotExist_ThrowDoctorDoesNotExistsException() {
        //given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(2L)
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .build();
        Visit existingVisit = Visit.builder()
                .id(1L)
                .doctor(doctor)
                .build();
        when(visitRepository.findById(existingVisit.getId())).thenReturn(Optional.of(existingVisit));
        when(doctorRepository.findById(command.getDoctorId())).thenReturn(Optional.empty());
        //when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> visitService.updateVisit(1L, command));
        //then
        assertAll(
                () -> assertEquals("Doctor does not exist.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void updateVisit_PatientDoesNotExist_ThrowPatientDoesNotExistsException() {
        //given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(1L)
                .patientId(5L)
                .build();
        Doctor doctor = Doctor.builder()
                .id(1L)
                .build();
        Visit existingVisit = Visit.builder()
                .id(1L)
                .doctor(doctor)
                .build();
        when(visitRepository.findById(existingVisit.getId())).thenReturn(Optional.of(existingVisit));
        when(patientRepository.findById(command.getPatientId())).thenReturn(Optional.empty());
        //when
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> visitService.updateVisit(1L, command));
        //then
        assertAll(
                () -> assertEquals("Patient not found.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void bookVisit_VisitDoesNotExist_ThrowVisitDoesNotExistsException() {
        //given
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        VisitDoesNotExistsException result = assertThrows(VisitDoesNotExistsException.class,
                () -> visitService.bookVisit(1L, 2L));
        //then
        assertAll(
                () -> assertEquals("Visit does not exist.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void bookVisit_VisitIsInThePast_ThrowInvalidVisitException() {
        //given
        Visit pastVisit = Visit.builder()
                .dateTime(LocalDateTime.now().minusDays(1))
                .build();
        when(visitRepository.findById(1L)).thenReturn(Optional.of(pastVisit));
        //when
        InvalidVisitException result = assertThrows(InvalidVisitException.class,
                () -> visitService.bookVisit(1L, 2L));
        //then
        assertAll(
                () -> assertEquals("Cannot book a visit in the past.", result.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, result.getHttpStatus())
        );
    }

    @Test
    void bookVisit_VisitAlreadyBooked_ThrowVisitUnavailableException() {
        //given
        Patient patient = Patient.builder()
                .id(5L)
                .build();
        Visit bookedVisit = Visit.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .patient(patient)
                .build();
        when(visitRepository.findById(1L)).thenReturn(Optional.of(bookedVisit));
        //when
        VisitUnavailableException result = assertThrows(VisitUnavailableException.class,
                () -> visitService.bookVisit(1L, 2L));
        //then
        assertAll(
                () -> assertEquals("This visit is already booked.", result.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, result.getHttpStatus())
        );
    }

    @Test
    void bookVisit_PatientDoesNotExist_ThrowPatientDoesNotExistsException() {
        //given
        Visit openVisit = Visit.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .patient(null)
                .build();
        when(visitRepository.findById(1L)).thenReturn(Optional.of(openVisit));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());
        //when
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> visitService.bookVisit(1L, 2L));
        //then
        assertAll(
                () -> assertEquals("Patient does not exist.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }

    @Test
    void delete_DataCorrect_DeleteVisit() {
        //given
        when(visitRepository.existsById(1L)).thenReturn(true);
        //when
        visitService.delete(1L);
        //then
        verify(visitRepository, times(1)).existsById(1L);
        verify(visitRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void delete_VisitDoesNotExists_ThrowVisitDoesNotExistsException() {
        //given
        when(visitRepository.existsById(1L)).thenReturn(false);
        //when
        VisitDoesNotExistsException result = assertThrows(VisitDoesNotExistsException.class,
                () -> visitService.delete(1L));
        //then
        assertAll(
                () -> assertEquals("Visit does not exist.", result.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getHttpStatus())
        );
    }
}
