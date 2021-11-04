package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.netcracker.backend.model.AuctionProcess;

import java.util.Optional;

public interface AuctionProcessRepository extends JpaRepository<AuctionProcess, Long> {
    @Query("select a from AuctionProcess a where a.auction.id = :auctionId")
    Optional<AuctionProcess> findAuctionProcessByAuction(@Param("auctionId") Long auctionId);
}
