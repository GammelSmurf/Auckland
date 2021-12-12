package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.entity.Tag;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByAuction_Id(Long auctionId);

    List<Tag> findAllByAuction_IdAndCategory_Id(Long auctionId, Long categoryId);
}
