package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.entity.Auction;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long>, JpaSpecificationExecutor<Auction> {
    Optional<Auction> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Auction a where a.id = ?1")
    Optional<Auction> findByIdWithPessimistic(Long id);

    boolean existsByName(String name);

    List<Auction> findAllByCreator_Username(String username);
}
