package com.kanapa4.medical_clinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

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

    public <R> Page<R> map(Function<T, R> converter) {
        List<R> mappedContent = this.content == null
                ? List.of()
                : this.content.stream().map(converter).toList();

        return Page.<R>builder()
                .content(mappedContent)
                .page(this.page)
                .size(this.size)
                .totalElements(this.totalElements)
                .totalPages(this.totalPages)
                .build();
    }
}
