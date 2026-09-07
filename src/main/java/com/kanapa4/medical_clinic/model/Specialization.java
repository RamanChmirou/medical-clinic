package com.kanapa4.medical_clinic.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Specialization {
    CARDIOLOGY("cardiology"),
    DERMATOLOGY("dermatology"),
    NEUROLOGIST("neurologist"),
    SURGEON("surgeon");
    private final String name;
}

