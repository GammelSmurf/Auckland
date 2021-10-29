package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {}
