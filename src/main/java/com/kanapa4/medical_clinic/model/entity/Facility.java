package com.kanapa4.medical_clinic.model.entity;

import com.kanapa4.medical_clinic.model.dto.FacilityCreateCommand;
import com.kanapa4.medical_clinic.model.dto.FacilityDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facilities")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String city;

    private String zipCode;

    private String street;

    private String buildingNumber;

    @Builder.Default
    @ManyToMany(mappedBy = "facilities")
    private Set<Doctor> doctors = new HashSet<>();

    public static Facility create(FacilityCreateCommand command) {
        return Facility.builder()
                .name(command.getName())
                .city(command.getCity())
                .zipCode(command.getZipCode())
                .street(command.getStreet())
                .buildingNumber(command.getBuildingNumber())
                .build();
    }

    public void update(FacilityDto dto) {
        this.name = dto.getName();
        this.city = dto.getCity();
        this.zipCode = dto.getZipCode();
        this.street = dto.getStreet();
        this.buildingNumber = dto.getBuildingNumber();
    }
}
