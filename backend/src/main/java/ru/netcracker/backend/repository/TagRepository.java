package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Category;
import ru.netcracker.backend.model.Tag;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByAuction_Id(Long auctionId);
    List<Tag> findAllByAuction_IdAndCategory_Id(Long auctionId, Long categoryId);
}
