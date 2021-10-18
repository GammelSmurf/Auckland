package ru.netcracker.backend.repositorie;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.user.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

}
