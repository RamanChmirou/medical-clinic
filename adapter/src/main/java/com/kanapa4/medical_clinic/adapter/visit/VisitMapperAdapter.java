package com.kanapa4.medical_clinic.adapter.visit;

import com.kanapa4.medical_clinic.domain.port.VisitMapperPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Visit;
import com.kanapa4.medical_clinic.model.VisitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VisitMapperAdapter implements VisitMapperPort {

    private final VisitMapper visitMapper;

    @Override
    public VisitDto toDto(Visit visit) {
        return visitMapper.toDto(visit);
    }

    @Override
    public Page<VisitDto> toPageDto(Page<Visit> page) {
        List<VisitDto> dtos = page.getContent().stream()
                .map(visitMapper::toDto)
                .toList();

        return Page.<VisitDto>builder()
                .content(dtos)
                .page(page.getPage())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}