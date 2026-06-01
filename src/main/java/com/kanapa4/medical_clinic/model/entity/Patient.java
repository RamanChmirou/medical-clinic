package com.kanapa4.medical_clinic.model.entity;

import com.kanapa4.medical_clinic.model.dto.PatientCreateCommand;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String idCardNo;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true)
    private String phoneNumber;
    @Column
    private LocalDate birthday;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public static Patient create(PatientCreateCommand dto, User user) {
        return Patient.builder()
                .idCardNo(dto.getIdCardNo())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .birthday(dto.getBirthday())
                .user(user)
                .build();
    }
}
