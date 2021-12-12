package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.entity.Bid;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
}
