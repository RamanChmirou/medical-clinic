package com.kanapa4.medical_clinic.repository.specs;

import com.kanapa4.medical_clinic.model.Specialization;
import com.kanapa4.medical_clinic.model.entity.Visit;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public final class VisitSpecs {

    private VisitSpecs() {
    }

    public static Specification<Visit> patientIdEq(Long patientId) {
        return patientId == null ? null
                : (root, q, cb) -> cb.equal(root.get("patient").get("id"), patientId);
    }

    public static Specification<Visit> doctorIdEq(Long doctorId) {
        return doctorId == null ? null
                : (root, q, cb) -> cb.equal(root.get("doctor").get("id"), doctorId);
    }

    public static Specification<Visit> doctorSpecializationEq(Specialization specialization) {
        return specialization == null ? null
                : (root, q, cb) -> {
            var doctorJoin = root.join("doctor");
            return cb.equal(doctorJoin.get("specialization"), specialization);
        };
    }

    public static Specification<Visit> dateEq(LocalDate date) {
        return date == null ? null
                : (root, q, cb) -> cb.between(
                root.get("dateTime"),
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        );
    }

    public static Specification<Visit> startsAfter(LocalDate startDate) {
        return startDate == null ? null
                : (root, q, cb) -> cb.greaterThanOrEqualTo(
                root.get("dateTime"),
                startDate.atStartOfDay()
        );
    }

    public static Specification<Visit> startsBefore(LocalDate endDate) {
        return endDate == null ? null
                : (root, q, cb) -> cb.lessThanOrEqualTo(
                root.get("dateTime"),
                endDate.atTime(LocalTime.MAX)
        );
    }

    public static Specification<Visit> isAvailable(Boolean available) {
        if (available == null) return null;
        return available
                ? (root, q, cb) -> cb.isNull(root.get("patient"))
                : (root, q, cb) -> cb.isNotNull(root.get("patient"));
    }
}
