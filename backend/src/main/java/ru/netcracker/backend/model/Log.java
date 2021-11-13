package ru.netcracker.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_logs")
@Getter
@Setter
public class Log {
    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logMessage;
    private LocalDateTime logTime;

    @OneToOne
    private Auction auction;
}
