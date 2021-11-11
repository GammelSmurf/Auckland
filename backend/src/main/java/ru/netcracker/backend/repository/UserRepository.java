package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User getUserById(Long id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    User findUserByVerificationCode(String code);
}
