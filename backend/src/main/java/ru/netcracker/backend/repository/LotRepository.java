package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.model.Lot;

public interface LotRepository extends JpaRepository<Lot, Long> {}
