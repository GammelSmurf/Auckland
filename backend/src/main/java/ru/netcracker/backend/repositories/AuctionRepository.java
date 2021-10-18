package ru.netcracker.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.netcracker.backend.models.Auction;

public interface AuctionRepository extends CrudRepository<Auction, Long> {

}
