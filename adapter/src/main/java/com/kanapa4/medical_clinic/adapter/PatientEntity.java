package com.kanapa4.medical_clinic.adapter;

import com.kanapa4.medical_clinic.model.PatientCreateCommand;
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
public class PatientEntity {
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
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    public static PatientEntity create(PatientCreateCommand dto, UserEntity user) {
        return PatientEntity.builder()
                .idCardNo(dto.getIdCardNo())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .birthday(dto.getBirthday())
                .user(user)
                .build();
    }
}
