package ru.netcracker.backend.repositorie;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.Tag;

public interface TagRepo extends JpaRepository<Tag, Long> {

}
