package ru.netcracker.backend.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private LocalDateTime dateTime = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus = TransactionStatus.STARTED;

    @ManyToOne
    private User buyer;
    @ManyToOne
    private User auctionCreator;
    @ManyToOne
    private Lot lot;

    public Transaction() {
    }

    public Transaction(Bid bid) {
        this.buyer = bid.getUser();
        this.amount = bid.getAmount();
        this.auctionCreator = bid.getAuction().getCreator();
        this.lot = bid.getLot();
    }
}
