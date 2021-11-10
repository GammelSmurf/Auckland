package ru.netcracker.backend.model;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.auction.Auction;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "auction_logs")
@Getter
@Setter
public class AuctionLog {
    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Auction auction;
    private String logMessage;
    private Date logTime;
}
