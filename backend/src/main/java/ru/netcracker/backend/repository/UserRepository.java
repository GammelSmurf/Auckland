package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.netcracker.backend.models.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    User getUserById(Long id);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    User findUserByVerificationCode(String code);
}