package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.domain.Tag;

public interface TagRepo extends JpaRepository<Tag, Long> {

}
