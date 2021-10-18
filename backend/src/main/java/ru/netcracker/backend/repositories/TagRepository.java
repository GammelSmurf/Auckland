package ru.netcracker.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.netcracker.backend.models.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {

}
