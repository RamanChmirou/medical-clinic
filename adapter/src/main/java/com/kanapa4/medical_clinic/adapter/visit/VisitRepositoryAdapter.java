package com.kanapa4.medical_clinic.adapter.visit;

import com.kanapa4.medical_clinic.adapter.PageMapper;
import com.kanapa4.medical_clinic.adapter.VisitEntity;
import com.kanapa4.medical_clinic.domain.port.VisitRepositoryPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.Visit;
import com.kanapa4.medical_clinic.model.VisitFilter;
import com.kanapa4.medical_clinic.model.exception.VisitAlreadyExistsException;
import lombok.RequiredArgsConstructor;
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

    @Override
    public Page<Visit> findAll(Pageable pageable) {
        var springPage = visitRepository.findAll(PageMapper.toSpringPageable(pageable));
        return PageMapper.toPojoPage(springPage, visitMapper::toPojo);
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
        var springPage = visitRepository.findAll(spec, PageMapper.toSpringPageable(pageable));
        return PageMapper.toPojoPage(springPage, visitMapper::toPojo);
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
        if (visitRepository.findById(visit.getId()).isPresent()) {
            throw new VisitAlreadyExistsException("Visit with id already exists");
        }
        VisitEntity entity = visitMapper.toEntity(visit);
        VisitEntity savedEntity = visitRepository.save(entity);
        return visitMapper.toPojo(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        visitRepository.deleteById(id);
    }
}