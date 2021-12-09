package ru.netcracker.backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class Transaction {
    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal price;
    private LocalDateTime dateTime = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus = TransactionStatus.WAIT;

    @ManyToOne
    private User buyer;

    public Transaction() {
    }

    public Transaction(Bid bid) {
        this.buyer = bid.getUser();
        this.price = bid.getAmount();
    }
}
