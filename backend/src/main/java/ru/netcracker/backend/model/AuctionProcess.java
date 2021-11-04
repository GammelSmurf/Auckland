package ru.netcracker.backend.model;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.auction.Auction;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "auction_processes")
@Getter
@Setter
public class AuctionProcess {
    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Auction auction;

    @OneToOne
    private Lot lot;

    private Time remainingTime;
    private Long currentBank;
}
