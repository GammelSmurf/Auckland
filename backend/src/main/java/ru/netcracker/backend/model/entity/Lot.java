package ru.netcracker.backend.model.entity;

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
    private String pictureLink;
    @Column(length = 10000)
    private String description;

    private BigDecimal minPrice = new BigDecimal(0);
    private BigDecimal priceIncreaseMinStep = new BigDecimal(0);
    private BigDecimal winPrice;
    private LocalDateTime endDateTime;

    private boolean finished = false;
    private boolean transferred = false;

    @OneToOne(mappedBy = "lot")
    private Bid bid;

    @OneToOne(mappedBy = "currentLot")
    @JsonBackReference
    private Auction auctionBeingPlayed;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    @JsonBackReference
    private Auction auction;
}
