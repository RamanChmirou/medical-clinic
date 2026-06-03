package com.kanapa4.medical_clinic.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Specialization {
    CARDIOLOGY("cardiology"),
    DERMATOLOGY("dermatology");

    private final String name;
}

