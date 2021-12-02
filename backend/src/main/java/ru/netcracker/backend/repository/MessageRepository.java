package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.model.Message;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> getMessagesByAuction_Id(Long auctionId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Message m WHERE m IN " +
            "(SELECT m FROM Message m INNER JOIN Auction a  ON m.auction=a WHERE a.status='FINISHED' and a.beginDate<:dateLineChat)" )
    void deleteOldChats(@Param("dateLineChat") LocalDateTime dateLineChat);
}
