package com.kanapa4.medical_clinic.model;

public enum Role {
    PATIENT("patient");

    private final String name;

    Role(String name) {
        this.name = name;
    }
}
