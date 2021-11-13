package ru.netcracker.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "bet")
@Getter
@Setter
public class Bet {
    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal currentBank = new BigDecimal(0);

    @OneToOne
    private Auction auction;

    @OneToOne
    private Lot lot;

    @OneToOne
    private User user;

    public Bet() {
    }

    public Bet(Auction auction) {
        this.auction = auction;
        this.lot = auction.getCurrentLot();
    }
}
