package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.AuctionLog;

import java.util.List;

@Repository
public interface AuctionLogRepository extends JpaRepository<AuctionLog, Long> {
    @Query("select a from AuctionLog a where a.auction.id = :auctionId")
    List<AuctionLog> findAuctionLogsByAuctionId(Long auctionId);
}
