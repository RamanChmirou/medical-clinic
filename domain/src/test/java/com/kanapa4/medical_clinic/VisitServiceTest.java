package com.kanapa4.medical_clinic;

import com.kanapa4.medical_clinic.domain.VisitService;
import com.kanapa4.medical_clinic.domain.VisitValidator;
import com.kanapa4.medical_clinic.domain.port.*;
import com.kanapa4.medical_clinic.model.*;
import com.kanapa4.medical_clinic.model.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VisitServiceTest {

    private VisitRepositoryPort visitRepositoryPort;
    private DoctorRepositoryPort doctorRepositoryPort;
    private PatientRepositoryPort patientRepositoryPort;
    private FacilityRepositoryPort facilityRepositoryPort;
    private VisitMapperPort visitMapperPort;
    private VisitValidator visitValidator;
    private VisitService visitService;

    @BeforeEach
    void setup() {
        this.visitRepositoryPort = mock(VisitRepositoryPort.class);
        this.doctorRepositoryPort = mock(DoctorRepositoryPort.class);
        this.patientRepositoryPort = mock(PatientRepositoryPort.class);
        this.facilityRepositoryPort = mock(FacilityRepositoryPort.class);
        this.visitMapperPort = mock(VisitMapperPort.class);
        this.visitValidator = mock(VisitValidator.class);

        this.visitService = new VisitService(
                visitRepositoryPort,
                doctorRepositoryPort,
                patientRepositoryPort,
                visitMapperPort,
                facilityRepositoryPort,
                visitValidator
        );
    }

    @Test
    void getPaginatedVisits_DataCorrect_ReturnPaginatedVisitDtos() {
        // given
        Pageable pageable = new Pageable(0, 10);
        Visit visit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        VisitDto visitDto = VisitDto.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();

        Page<Visit> domainPage = Page.<Visit>builder()
                .content(List.of(visit))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        Page<VisitDto> dtoPage = Page.<VisitDto>builder()
                .content(List.of(visitDto))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(visitRepositoryPort.findAll(pageable)).thenReturn(domainPage);
        when(visitMapperPort.toPageDto(domainPage)).thenReturn(dtoPage);

        // when
        Page<VisitDto> result = visitService.getPaginatedVisits(pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(visitDto.getDateTime(), result.getContent().getFirst().getDateTime()),
                () -> assertEquals(visitDto.getDurationInMinutes(), result.getContent().getFirst().getDurationInMinutes())
        );
        verify(visitRepositoryPort, times(1)).findAll(pageable);
        verify(visitMapperPort, times(1)).toPageDto(domainPage);
        verifyNoMoreInteractions(visitRepositoryPort, visitMapperPort);
    }

    @Test
    void searchVisits_DataCorrect_ReturnPageOfVisitDtos() {
        // given
        VisitFilter filter = new VisitFilter(
                1L, 2L, Specialization.CARDIOLOGY,
                LocalDate.now(), LocalDate.now(), LocalDate.now(), true
        );
        Pageable pageable = new Pageable(0, 10);
        Visit visit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        VisitDto visitDto = VisitDto.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();

        Page<Visit> domainPage = Page.<Visit>builder()
                .content(List.of(visit))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        Page<VisitDto> dtoPage = Page.<VisitDto>builder()
                .content(List.of(visitDto))
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(visitRepositoryPort.findAll(filter, pageable)).thenReturn(domainPage);
        when(visitMapperPort.toPageDto(domainPage)).thenReturn(dtoPage);

        // when
        Page<VisitDto> result = visitService.searchVisits(filter, pageable);

        // then
        assertAll(
                () -> assertEquals(1, result.getTotalPages()),
                () -> assertEquals(visitDto.getDateTime(), result.getContent().getFirst().getDateTime()),
                () -> assertEquals(visitDto.getDurationInMinutes(), result.getContent().getFirst().getDurationInMinutes())
        );
        verify(visitRepositoryPort, times(1)).findAll(filter, pageable);
        verify(visitMapperPort, times(1)).toPageDto(domainPage);
        verifyNoMoreInteractions(visitRepositoryPort, visitMapperPort);
    }

    @Test
    void createVisitSlot_DataCorrect_ReturnCreatedVisitDto() {
        // given
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
        VisitDto expectedDto = VisitDto.builder()
                .id(10L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .doctorId(1L)
                .facilityId(2L)
                .build();

        when(doctorRepositoryPort.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityRepositoryPort.findById(2L)).thenReturn(Optional.of(facility));
        when(visitRepositoryPort.save(any(Visit.class))).thenReturn(savedVisit);
        when(visitMapperPort.toDto(savedVisit)).thenReturn(expectedDto);

        // when
        VisitDto result = visitService.createVisitSlot(command);

        // then
        assertAll(
                () -> assertEquals(command.getDateTime(), result.getDateTime()),
                () -> assertEquals(command.getDurationInMinutes(), result.getDurationInMinutes())
        );
        verify(visitValidator, times(1)).validateVisitTimeAndDuration(command.getDateTime(), command.getDurationInMinutes());
        verify(visitValidator, times(1)).checkDoctorAvailability(command.getDoctorId(), command.getDateTime(), command.getDurationInMinutes(), null);
        verify(doctorRepositoryPort, times(1)).findById(1L);
        verify(facilityRepositoryPort, times(1)).findById(2L);
        verify(visitRepositoryPort, times(1)).save(any(Visit.class));
        verify(visitMapperPort, times(1)).toDto(savedVisit);
        verifyNoMoreInteractions(visitRepositoryPort, doctorRepositoryPort, facilityRepositoryPort, visitMapperPort);
    }

    @Test
    void createVisitSlot_DoctorDoesNotExist_ThrowDoctorDoesNotExistsException() {
        // given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(1L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        when(doctorRepositoryPort.findById(command.getDoctorId())).thenReturn(Optional.empty());

        // when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> visitService.createVisitSlot(command));

        // then
        assertAll(
                () -> assertEquals("Doctor does not exist.", result.getMessage())
        );
    }

    @Test
    void createVisitSlot_FacilityDoesNotExist_ThrowFacilityDoesNotExistsException() {
        // given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(1L)
                .facilityId(2L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        Doctor doctor = Doctor.builder().id(1L).build();
        when(doctorRepositoryPort.findById(command.getDoctorId())).thenReturn(Optional.of(doctor));
        when(facilityRepositoryPort.findById(command.getFacilityId())).thenReturn(Optional.empty());

        // when
        FacilityDoesNotExistsException result = assertThrows(FacilityDoesNotExistsException.class,
                () -> visitService.createVisitSlot(command));

        // then
        assertAll(
                () -> assertEquals("Facility does not exist.", result.getMessage())
        );
    }

    @Test
    void updateVisit_DataCorrect_ReturnUpdatedVisitDto() {
        // given
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
        VisitDto expectedDto = VisitDto.builder()
                .id(1L)
                .dateTime(LocalDateTime.of(2026, 7, 8, 14, 0))
                .durationInMinutes(30)
                .doctorId(20L)
                .patientId(5L)
                .build();

        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(existingVisit));
        when(doctorRepositoryPort.findById(20L)).thenReturn(Optional.of(newDoctor));
        when(patientRepositoryPort.findById(5L)).thenReturn(Optional.of(patient));
        when(visitRepositoryPort.save(any(Visit.class))).thenReturn(updatedVisit);
        when(visitMapperPort.toDto(updatedVisit)).thenReturn(expectedDto);

        // when
        VisitDto result = visitService.updateVisit(1L, command);

        // then
        assertAll(
                () -> assertEquals(command.getDateTime(), result.getDateTime()),
                () -> assertEquals(command.getDurationInMinutes(), result.getDurationInMinutes())
        );
        verify(visitValidator, times(1)).validateVisitTimeAndDuration(command.getDateTime(), command.getDurationInMinutes());
        verify(visitValidator, times(1)).checkDoctorAvailability(command.getDoctorId(), command.getDateTime(), command.getDurationInMinutes(), 1L);
        verify(visitRepositoryPort, times(1)).findById(1L);
        verify(doctorRepositoryPort, times(1)).findById(20L);
        verify(patientRepositoryPort, times(1)).findById(5L);
        verify(visitRepositoryPort, times(1)).save(any(Visit.class));
        verify(visitMapperPort, times(1)).toDto(updatedVisit);
        verifyNoMoreInteractions(visitRepositoryPort, doctorRepositoryPort, patientRepositoryPort, visitMapperPort);
    }

    @Test
    void updateVisit_VisitDoesNotExist_ThrowVisitDoesNotExistsException() {
        // given
        VisitCreateCommand command = VisitCreateCommand.builder().build();
        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // when
        VisitDoesNotExistsException result = assertThrows(VisitDoesNotExistsException.class,
                () -> visitService.updateVisit(1L, command));

        // then
        assertAll(
                () -> assertEquals("Visit does not exist.", result.getMessage())
        );
    }

    @Test
    void updateVisit_DoctorDoesNotExist_ThrowDoctorDoesNotExistsException() {
        // given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(2L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        Doctor doctor = Doctor.builder().id(1L).build();
        Visit existingVisit = Visit.builder().id(1L).doctor(doctor).build();

        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(existingVisit));
        when(doctorRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        // when
        DoctorDoesNotExistsException result = assertThrows(DoctorDoesNotExistsException.class,
                () -> visitService.updateVisit(1L, command));

        // then
        assertAll(
                () -> assertEquals("Doctor does not exist.", result.getMessage())
        );
    }

    @Test
    void updateVisit_PatientDoesNotExist_ThrowPatientDoesNotExistsException() {
        // given
        VisitCreateCommand command = VisitCreateCommand.builder()
                .doctorId(1L)
                .patientId(5L)
                .dateTime(LocalDateTime.of(2026, 7, 7, 12, 0))
                .durationInMinutes(15)
                .build();
        Doctor doctor = Doctor.builder().id(1L).build();
        Visit existingVisit = Visit.builder().id(1L).doctor(doctor).build();

        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(existingVisit));
        when(patientRepositoryPort.findById(5L)).thenReturn(Optional.empty());

        // when
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> visitService.updateVisit(1L, command));

        // then
        assertAll(
                () -> assertEquals("Patient not found.", result.getMessage())
        );
    }

    @Test
    void bookVisit_DataCorrect_ReturnBookedVisitDto() {
        // given
        Patient patient = Patient.builder().id(2L).build();
        Visit openVisit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().plusDays(1))
                .patient(null)
                .build();
        VisitDto expectedDto = VisitDto.builder()
                .id(1L)
                .patientId(2L)
                .build();

        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(openVisit));
        when(patientRepositoryPort.findById(2L)).thenReturn(Optional.of(patient));
        when(visitRepositoryPort.save(openVisit)).thenReturn(openVisit);
        when(visitMapperPort.toDto(openVisit)).thenReturn(expectedDto);

        // when
        VisitDto result = visitService.bookVisit(1L, 2L);

        // then
        assertAll(
                () -> assertEquals(patient.getId(), result.getPatientId()),
                () -> assertEquals(patient, openVisit.getPatient())
        );
        verify(visitRepositoryPort, times(1)).findById(1L);
        verify(patientRepositoryPort, times(1)).findById(2L);
        verify(visitRepositoryPort, times(1)).save(openVisit);
        verify(visitMapperPort, times(1)).toDto(openVisit);
        verifyNoMoreInteractions(visitRepositoryPort, patientRepositoryPort, visitMapperPort);
    }

    @Test
    void bookVisit_VisitDoesNotExist_ThrowVisitDoesNotExistsException() {
        // given
        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // when
        VisitDoesNotExistsException result = assertThrows(VisitDoesNotExistsException.class,
                () -> visitService.bookVisit(1L, 2L));

        // then
        assertAll(
                () -> assertEquals("Visit does not exist.", result.getMessage())
        );
    }

    @Test
    void bookVisit_VisitIsInThePast_ThrowInvalidVisitException() {
        // given
        Visit pastVisit = Visit.builder()
                .dateTime(LocalDateTime.now().minusDays(1))
                .build();
        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(pastVisit));

        // when
        InvalidVisitException result = assertThrows(InvalidVisitException.class,
                () -> visitService.bookVisit(1L, 2L));

        // then
        assertAll(
                () -> assertEquals("Cannot book a visit in the past.", result.getMessage())
        );
    }

    @Test
    void bookVisit_VisitAlreadyBooked_ThrowVisitUnavailableException() {
        // given
        Patient patient = Patient.builder().id(5L).build();
        Visit bookedVisit = Visit.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .patient(patient)
                .build();
        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(bookedVisit));

        // when
        VisitUnavailableException result = assertThrows(VisitUnavailableException.class,
                () -> visitService.bookVisit(1L, 2L));

        // then
        assertAll(
                () -> assertEquals("This visit is already booked.", result.getMessage())
        );
    }

    @Test
    void bookVisit_PatientDoesNotExist_ThrowPatientDoesNotExistsException() {
        // given
        Visit openVisit = Visit.builder()
                .dateTime(LocalDateTime.now().plusDays(1))
                .patient(null)
                .build();
        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(openVisit));
        when(patientRepositoryPort.findById(2L)).thenReturn(Optional.empty());

        // when
        PatientDoesNotExistsException result = assertThrows(PatientDoesNotExistsException.class,
                () -> visitService.bookVisit(1L, 2L));

        // then
        assertAll(
                () -> assertEquals("Patient does not exist.", result.getMessage())
        );
    }

    @Test
    void cancelVisit_DataCorrect_ReturnCancelledVisitDto() {
        // given
        Patient patient = Patient.builder().id(2L).build();
        Visit bookedVisit = Visit.builder()
                .id(1L)
                .dateTime(LocalDateTime.now().plusDays(1))
                .patient(patient)
                .build();
        Visit cancelledVisit = Visit.builder()
                .id(1L)
                .dateTime(bookedVisit.getDateTime())
                .patient(null)
                .build();
        VisitDto expectedDto = VisitDto.builder()
                .id(1L)
                .patientId(null)
                .build();

        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(bookedVisit));
        when(visitRepositoryPort.save(any(Visit.class))).thenReturn(cancelledVisit);
        when(visitMapperPort.toDto(cancelledVisit)).thenReturn(expectedDto);

        // when
        VisitDto result = visitService.cancelVisit(1L);

        // then
        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(null, result.getPatientId())
        );
        verify(visitRepositoryPort, times(1)).findById(1L);
        verify(visitRepositoryPort, times(1)).save(bookedVisit);
        verify(visitMapperPort, times(1)).toDto(cancelledVisit);
        verifyNoMoreInteractions(visitRepositoryPort, visitMapperPort);
    }

    @Test
    void cancelVisit_VisitDoesNotExist_ThrowVisitDoesNotExistsException() {
        // given
        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // when
        VisitDoesNotExistsException result = assertThrows(VisitDoesNotExistsException.class,
                () -> visitService.cancelVisit(1L));

        // then
        assertAll(
                () -> assertEquals("Visit does not exist", result.getMessage())
        );
    }

    @Test
    void cancelVisit_VisitHasNoPatient_ThrowVisitUnavailableException() {
        // given
        Visit emptyVisit = Visit.builder()
                .id(1L)
                .patient(null)
                .build();
        when(visitRepositoryPort.findById(1L)).thenReturn(Optional.of(emptyVisit));

        // when
        VisitUnavailableException result = assertThrows(VisitUnavailableException.class,
                () -> visitService.cancelVisit(1L));

        // then
        assertAll(
                () -> assertEquals("Cannot cancel a visit that has no patient booked.", result.getMessage())
        );
    }

    @Test
    void delete_DataCorrect_DeleteVisit() {
        // given
        when(visitRepositoryPort.existsById(1L)).thenReturn(true);

        // when
        visitService.delete(1L);

        // then
        verify(visitRepositoryPort, times(1)).existsById(1L);
        verify(visitRepositoryPort, times(1)).deleteById(1L);
        verifyNoMoreInteractions(visitRepositoryPort);
    }

    @Test
    void delete_VisitDoesNotExists_ThrowVisitDoesNotExistsException() {
        // given
        when(visitRepositoryPort.existsById(1L)).thenReturn(false);

        // when
        VisitDoesNotExistsException result = assertThrows(VisitDoesNotExistsException.class,
                () -> visitService.delete(1L));

        // then
        assertAll(
                () -> assertEquals("Visit does not exist.", result.getMessage())
        );
    }
}