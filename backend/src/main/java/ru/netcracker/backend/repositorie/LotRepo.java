package ru.netcracker.backend.repositorie;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.Lot;

public interface LotRepo extends JpaRepository<Lot, Long> {

}
