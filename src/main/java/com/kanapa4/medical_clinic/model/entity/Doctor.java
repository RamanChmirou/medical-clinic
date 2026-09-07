package com.kanapa4.medical_clinic.model.entity;

import com.kanapa4.medical_clinic.model.Specialization;
import com.kanapa4.medical_clinic.model.dto.DoctorCreateCommand;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String firstName;

    private String lastName;
    
    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "doctor_facility",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();

    public void addFacility(Facility facility) {
        this.facilities.add(facility);
        facility.getDoctors().add(this);
    }

    public void removeFacility(Facility facility) {
        this.facilities.remove(facility);
        facility.getDoctors().remove(this);
    }

    public static Doctor create(DoctorCreateCommand dto, User user) {
        return Doctor.builder()
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .specialization(dto.getSpecialization())
                .build();
    }
}
