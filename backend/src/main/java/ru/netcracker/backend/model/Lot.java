package ru.netcracker.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lots")
@Getter
@Setter
public class Lot {
    @Id
    @Column(name = "lot_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String picture;
    @Column(length = 10000)
    private String description;

    private BigDecimal minBank;
    private BigDecimal step;
    private LocalDateTime endTime;

    private boolean finished = false;
    private BigDecimal winBank;

    @OneToOne(mappedBy = "lot")
    private Bet bet;

    @OneToOne(mappedBy = "currentLot")
    private Auction auctionLot;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;
}
