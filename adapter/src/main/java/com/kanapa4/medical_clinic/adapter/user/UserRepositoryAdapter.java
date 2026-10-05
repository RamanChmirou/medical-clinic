package com.kanapa4.medical_clinic.adapter.user;

import com.kanapa4.medical_clinic.adapter.UserEntity;
import com.kanapa4.medical_clinic.domain.port.UserRepositoryPort;
import com.kanapa4.medical_clinic.model.Page;
import com.kanapa4.medical_clinic.model.Pageable;
import com.kanapa4.medical_clinic.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<User> findAll(Pageable pageable) {
        var springPageable = PageRequest.of(pageable.getPage(), pageable.getSize());
        var springPage = userRepository.findAll(springPageable);
        List<User> domainUsers = springPage.getContent().stream()
                .map(userMapper::toPojo)
                .toList();

        return Page.<User>builder()
                .content(domainUsers)
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .build();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toPojo);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toPojo);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity;
        if (user.getId() == null) {
            entity = userMapper.toEntity(user);
        } else {
            entity = userRepository.findById(user.getId()).orElseThrow();
            entity.setEmail(user.getEmail());
            entity.setPassword(user.getPassword());
            entity.setRole(user.getRole());
        }
        UserEntity savedEntity = userRepository.save(entity);
        return userMapper.toPojo(savedEntity);
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
}