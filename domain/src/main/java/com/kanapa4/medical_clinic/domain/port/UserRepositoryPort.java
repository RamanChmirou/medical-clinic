package com.kanapa4.medical_clinic.domain.port;

import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Page<User> findAll(Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User save(User user);

    void deleteByEmail(String email);
}
