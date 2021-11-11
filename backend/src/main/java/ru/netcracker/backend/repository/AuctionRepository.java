package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.auction.Auction;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {}
