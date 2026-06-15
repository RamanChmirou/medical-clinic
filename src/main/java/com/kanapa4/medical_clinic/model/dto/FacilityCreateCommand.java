package com.kanapa4.medical_clinic.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityCreateCommand {
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;
}
