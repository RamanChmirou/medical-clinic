package com.kanapa4.medical_clinic.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    PATIENT("patient"),
    DOCTOR("doctor");

    private final String name;
}
