package ru.netcracker.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.entity.Lot;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    List<Lot> findAllByAuction_Id(Long auctionId);
    List<Lot> findAllByWinner_UsernameAndTransferred(String username, boolean transferred);
}
