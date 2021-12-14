package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.netcracker.backend.model.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser_Username(String username);

    @Modifying
    void deleteAllByDateTimeIsLessThan(LocalDateTime time);
}
