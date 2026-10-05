package com.kanapa4.medical_clinic.model;

import lombok.Getter;

@Getter
public class Pageable {
    private Integer page;
    private Integer size;

    public Pageable(Integer page, Integer size) {
        this.page = page;
        if (size > 30) {
            this.size = 30;
        } else {
            this.size = size;
        }
    }
}
