package com.kanapa4.medical_clinic.adapter.config;

import com.kanapa4.medical_clinic.domain.*;
import com.kanapa4.medical_clinic.domain.port.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class AppConfig {

    @Bean
    @Transactional
    public UserService userService(UserRepositoryPort userRepositoryPort,
                                   UserMapperPort userMapperPort) {
        return new UserService(userRepositoryPort, userMapperPort);
    }

    @Bean
    @Transactional
    public PatientService patientService(PatientRepositoryPort patientRepositoryPort,
                                         PatientMapperPort patientMapperPort,
                                         UserRepositoryPort userRepositoryPort) {
        return new PatientService(patientRepositoryPort, patientMapperPort, userRepositoryPort);
    }

    @Bean
    @Transactional
    public FacilityService facilityService(FacilityRepositoryPort facilityRepositoryPort,
                                           FacilityMapperPort facilityMapperPort) {
        return new FacilityService(facilityRepositoryPort, facilityMapperPort);
    }

    @Bean
    @Transactional
    public DoctorService doctorService(DoctorRepositoryPort doctorRepositoryPort,
                                       UserRepositoryPort userRepositoryPort,
                                       FacilityRepositoryPort facilityRepositoryPort,
                                       DoctorMapperPort doctorMapperPort) {
        return new DoctorService(doctorRepositoryPort, userRepositoryPort, facilityRepositoryPort, doctorMapperPort);
    }

    @Bean
    public VisitValidator visitValidator(VisitRepositoryPort visitRepositoryPort) {
        return new VisitValidator(visitRepositoryPort);
    }

    @Bean
    @Transactional
    public VisitService visitService(VisitRepositoryPort visitRepositoryPort,
                                     DoctorRepositoryPort doctorRepositoryPort,
                                     PatientRepositoryPort patientRepositoryPort,
                                     VisitMapperPort visitMapperPort,
                                     FacilityRepositoryPort facilityRepositoryPort,
                                     VisitValidator visitValidator) {
        return new VisitService(visitRepositoryPort, doctorRepositoryPort, patientRepositoryPort,
                visitMapperPort, facilityRepositoryPort, visitValidator);
    }
}