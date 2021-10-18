package ru.netcracker.backend.Models;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.Models.User.User;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="lots")
@Getter
@Setter
public class Lot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name="auction_id")
    private Auction auction;

    private String name;
    private String picture;
    private String description;
    private BigDecimal min_bank;
    private BigDecimal step;
}
