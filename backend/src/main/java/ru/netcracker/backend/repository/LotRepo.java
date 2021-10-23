package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.domain.Lot;

public interface LotRepo extends JpaRepository<Lot, Long> {

}
