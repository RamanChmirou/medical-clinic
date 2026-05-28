package com.kanapa4.medical_clinic.model.dto;

import com.kanapa4.medical_clinic.model.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientCreateCommand {
    private String firstName;
    private String lastName;
    private String idCardNo;
    private LocalDate birthday;
    private String phoneNumber;
    private User user;
}
