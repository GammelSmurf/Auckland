package ru.netcracker.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.netcracker.backend.models.user.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

}
