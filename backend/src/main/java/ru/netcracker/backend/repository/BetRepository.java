package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.Bet;

import java.util.Optional;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    @Query("select a from Bet a where a.auction.id = :auctionId")
    Optional<Bet> findBetByAuctionId(@Param("auctionId") Long auctionId);
}
