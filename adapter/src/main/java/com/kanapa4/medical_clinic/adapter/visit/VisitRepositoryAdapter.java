package com.kanapa4.medical_clinic.adapter.visit;

import com.kanapa4.medical_clinic.adapter.VisitEntity;
import com.kanapa4.medical_clinic.adapter.doctor.DoctorRepository;
import com.kanapa4.medical_clinic.adapter.facility.FacilityRepository;
import com.kanapa4.medical_clinic.adapter.patient.PatientRepository;
import com.kanapa4.medical_clinic.domain.port.VisitRepositoryPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Visit;
import com.kanapa4.medical_clinic.model.VisitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitRepositoryAdapter implements VisitRepositoryPort {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public Page<Visit> findAll(Pageable pageable) {
        var springPageable = PageRequest.of(pageable.getPage(), pageable.getSize());
        return visitMapper.toPojoPage(visitRepository.findAll(springPageable));
    }

    @Override
    public Page<Visit> findAll(VisitFilter filter, Pageable pageable) {
        Specification<VisitEntity> spec = Specification.allOf(
                VisitSpecs.patientIdEq(filter.patientId()),
                VisitSpecs.doctorIdEq(filter.doctorId()),
                VisitSpecs.doctorSpecializationEq(filter.specialization()),
                VisitSpecs.dateEq(filter.date()),
                VisitSpecs.startsAfter(filter.startDate()),
                VisitSpecs.startsBefore(filter.endDate()),
                VisitSpecs.isAvailable(filter.available())
        );

        var springPageable = PageRequest.of(pageable.getPage(), pageable.getSize());
        return visitMapper.toPojoPage(visitRepository.findAll(spec, springPageable));
    }

    @Override
    public Optional<Visit> findById(Long id) {
        return visitRepository.findById(id).map(visitMapper::toPojo);
    }

    @Override
    public List<Visit> findAllByDoctorIdAndDate(Long doctorId, LocalDateTime dayStart, LocalDateTime dayEnd) {
        return visitRepository.findAllByDoctorIdAndDate(doctorId, dayStart, dayEnd).stream()
                .map(visitMapper::toPojo)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return visitRepository.existsById(id);
    }

    @Override
    @Transactional
    public Visit save(Visit visit) {
        VisitEntity entity = visitMapper.toEntity(visit);
        entity.setDoctorEntity(doctorRepository.getReferenceById(visit.getDoctor().getId()));
        entity.setFacility(facilityRepository.getReferenceById(visit.getFacility().getId()));
        entity.setPatientEntity(visit.getPatient() == null
                ? null
                : patientRepository.getReferenceById(visit.getPatient().getId()));
        VisitEntity saved = visitRepository.save(entity);
        return visitMapper.toPojo(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        visitRepository.deleteById(id);
    }
}