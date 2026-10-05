package com.kanapa4.medical_clinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Page<T> {
    private List<T> content;
    private Integer size;
    private Integer page;
    private Long totalElements;
    private Integer totalPages;
}
