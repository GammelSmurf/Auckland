package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.model.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {}
