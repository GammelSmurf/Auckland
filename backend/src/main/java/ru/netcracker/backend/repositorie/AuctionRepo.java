package ru.netcracker.backend.repositorie;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.models.Auction;

public interface AuctionRepo extends JpaRepository<Auction, Long> {

}
