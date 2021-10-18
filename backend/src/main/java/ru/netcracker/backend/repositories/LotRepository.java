package ru.netcracker.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.netcracker.backend.models.Lot;

public interface LotRepository extends CrudRepository<Lot, Long> {

}
