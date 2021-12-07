package ru.netcracker.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.Auction;

import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findById(Long id);
    Page<Auction> findByCreator_Username(String username, Pageable pageable);
    Page<Auction> findBySubscribers_Username(String username, Pageable pageable);
    @Query("select a from Auction a where lower(concat(a.name,' ',a.description,' ',a.status, ' ', a.beginDate, ' ', " +
            "a.endDate, ' ', a.boostTime, ' ', a.usersLimit)) like lower(concat('%', ?1,'%'))")
    Page<Auction> search(String keyword, Pageable pageable);
}
