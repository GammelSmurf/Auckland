package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.domain.user.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

}
