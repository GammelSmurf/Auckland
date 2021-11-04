package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.model.auction.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
