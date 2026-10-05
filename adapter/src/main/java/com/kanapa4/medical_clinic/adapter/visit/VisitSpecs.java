package com.kanapa4.medical_clinic.adapter.visit;

import com.kanapa4.medical_clinic.adapter.VisitEntity;
import com.kanapa4.medical_clinic.model.Specialization;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public final class VisitSpecs {

    private VisitSpecs() {
    }

    public static Specification<VisitEntity> patientIdEq(Long patientId) {
        return patientId == null ? null
                : (root, q, cb) -> cb.equal(root.get("patientEntity").get("id"), patientId);
    }

    public static Specification<VisitEntity> doctorIdEq(Long doctorId) {
        return doctorId == null ? null
                : (root, q, cb) -> cb.equal(root.get("doctorEntity").get("id"), doctorId);
    }

    public static Specification<VisitEntity> doctorSpecializationEq(Specialization specialization) {
        return specialization == null ? null
                : (root, q, cb) -> {
            var doctorJoin = root.join("doctorEntity");
            return cb.equal(doctorJoin.get("specialization"), specialization);
        };
    }

    public static Specification<VisitEntity> dateEq(LocalDate date) {
        return date == null ? null
                : (root, q, cb) -> cb.between(
                root.get("dateTime"),
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        );
    }

    public static Specification<VisitEntity> startsAfter(LocalDate startDate) {
        return startDate == null ? null
                : (root, q, cb) -> cb.greaterThanOrEqualTo(
                root.get("dateTime"),
                startDate.atStartOfDay()
        );
    }

    public static Specification<VisitEntity> startsBefore(LocalDate endDate) {
        return endDate == null ? null
                : (root, q, cb) -> cb.lessThanOrEqualTo(
                root.get("dateTime"),
                endDate.atTime(LocalTime.MAX)
        );
    }

    public static Specification<VisitEntity> isAvailable(Boolean available) {
        if (available == null) return null;
        return available
                ? (root, q, cb) -> cb.isNull(root.get("patientEntity"))
                : (root, q, cb) -> cb.isNotNull(root.get("patientEntity"));
    }
}