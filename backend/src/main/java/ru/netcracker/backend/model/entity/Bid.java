package ru.netcracker.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bids")
@Getter
@Setter
public class Bid {
    @Id
    @Column(name = "bid_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount = new BigDecimal(0);

    @OneToOne
    @JoinColumn(name="auction_id")
    @JsonBackReference
    private Auction auction;

    @OneToOne
    @JoinColumn(name="lot_id")
    @JsonBackReference
    private Lot lot;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;

    @OneToMany
    @JoinColumn(name="bid_id")
    @JsonBackReference
    private Set<Transaction> transactions = new HashSet<>();

    public Bid() {
    }

    public Bid(Auction auction) {
        this.auction = auction;
        this.lot = auction.getCurrentLot();
    }

    public void updateWithAnotherBidRequest(BigDecimal amount, User user, Auction auction) {
        setAmount(this.amount.add(amount));
        addExtraTime();
        setUser(user);
        setAuction(auction);
        auction.setCurrentBid(this);
        user.subtractMoney(amount);
    }

    public void addExtraTime() {
        getLot().setEndDateTime(getEndTimeWithExtra());
    }

    private LocalDateTime getEndTimeWithExtra() {
        return getLot()
                .getEndDateTime()
                .plus(getAuction()
                        .getExtraTime().toNanoOfDay(), ChronoUnit.NANOS);
    }
}
