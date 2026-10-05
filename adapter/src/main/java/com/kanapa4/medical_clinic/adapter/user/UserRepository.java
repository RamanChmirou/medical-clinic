package com.kanapa4.medical_clinic.adapter.user;

import com.kanapa4.medical_clinic.adapter.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    void deleteByEmail(@NonNull String email);
}
