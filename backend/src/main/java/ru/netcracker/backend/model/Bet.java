package ru.netcracker.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
    @JsonBackReference
    private Auction auction;

    @OneToOne
    @JsonBackReference
    private Lot lot;

    @OneToOne
    @JsonBackReference
    private User user;

    @OneToMany
    private Set<Transaction> transactions = new HashSet<>();

    public Bet() {
    }

    public Bet(Auction auction) {
        this.auction = auction;
        this.lot = auction.getCurrentLot();
    }
}
