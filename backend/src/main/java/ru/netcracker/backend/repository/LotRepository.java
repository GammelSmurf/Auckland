package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.entity.Lot;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    List<Lot> findAllByAuction_Id(Long auctionId);
    @Query("select l from Lot l where (l.winner.username = ?1 or l.auction.creator.username = ?1) and l.transferred = ?2")
    List<Lot> findAllIfWinnerOrCreatorByTransferred(String username, boolean transferred);
}
