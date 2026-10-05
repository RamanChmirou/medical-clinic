package com.kanapa4.medical_clinic.adapter;

import com.kanapa4.medical_clinic.model.Specialization;
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
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

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
    private Set<FacilityEntity> facilities = new HashSet<>();

    public void addFacility(FacilityEntity facility) {
        this.facilities.add(facility);
        facility.getDoctors().add(this);
    }

    public void removeFacility(FacilityEntity facility) {
        this.facilities.remove(facility);
        facility.getDoctors().remove(this);
    }
}