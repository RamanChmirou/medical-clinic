package com.kanapa4.medical_clinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityCreateCommand {
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;
}
