package com.kanapa4.medical_clinic.adapter;

import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.function.Function;

public class PageMapper {

    public static org.springframework.data.domain.Pageable toSpringPageable(Pageable pageable) {
        return PageRequest.of(pageable.getPage(), pageable.getSize());
    }

    public static <S, T> Page<T> toPojoPage(org.springframework.data.domain.Page<S> springPage,
                                            Function<S, T> mapper) {
        return Page.<T>builder()
                .content(springPage.getContent().stream().map(mapper).toList())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .build();
    }
}