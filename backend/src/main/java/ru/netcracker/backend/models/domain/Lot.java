package ru.netcracker.backend.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="lots")
@Getter
@Setter
public class Lot {

    @Id
    @Column(name="lot_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="auction_id", nullable = false)
    private Auction auction;

    private String name;
    private String picture;
    private String description;
    private Long minBank;
    private Long step;
}
