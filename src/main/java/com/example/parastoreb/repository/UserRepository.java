package com.example.parastoreb.repository;

import com.example.parastoreb.entity.Role;
import com.example.parastoreb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByRole(Role role);
    long countByRole(Role role);
    List<User> findByRole(Role role);
    void deleteByEmail(String email);
}
