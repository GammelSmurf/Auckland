package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.Auction;

public interface AuctionRepo extends JpaRepository<Auction, Long> {

}