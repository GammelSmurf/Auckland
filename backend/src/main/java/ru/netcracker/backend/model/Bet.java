package ru.netcracker.backend.model;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.model.user.User;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "auction_processes")
@Getter
@Setter
public class Bet {
    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Auction auction;

    @OneToOne
    private Lot lot;

    @OneToOne
    private User user;

    private Time remainingTime;
    private Long currentBank;
}
