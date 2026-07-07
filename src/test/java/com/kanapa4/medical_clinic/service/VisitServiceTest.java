package com.kanapa4.medical_clinic.service;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    }
}
