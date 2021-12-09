package ru.netcracker.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.exception.auction.NoLotsException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "auctions")
@Getter
@Setter
public class Auction {
    @Id
    @Column(name = "auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 10000)
    private String description;

    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    private LocalTime lotDuration;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.DRAFT;

    private LocalTime boostTime;
    private Long usersNumberLimit;

    @OneToOne(mappedBy = "auction")
    @JsonBackReference
    private Bid bid;

    @OneToMany(mappedBy = "auction")
    @JsonBackReference
    private Set<Log> logs = new HashSet<>();

    @OneToOne
    @JsonBackReference
    private Lot currentLot;

    @OneToMany(
            mappedBy = "auction",
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Lot> lots = new ArrayList<>(0);

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @OneToMany(
            mappedBy = "auction",
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "ratings",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> usersWhoLiked = new HashSet<>();

    @JsonBackReference
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> subscribers = new HashSet<>();

    @JsonBackReference
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "auction_categories",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public int getLikesCount() {
        return this.getUsersWhoLiked().size();
    }

    public int getSubscribersCount() {
        return getSubscribers().size();
    }

    public boolean isDraft() {
        return status == AuctionStatus.DRAFT;
    }

    public boolean isWaiting() {
        return status == AuctionStatus.WAITING;
    }

    public boolean isFinished() {
        return status == AuctionStatus.FINISHED;
    }

    public boolean isRunning() {
        return status == AuctionStatus.RUNNING;
    }

    public Optional<Lot> getAnotherLot() {
        return getLots().stream()
                .filter(lot -> !lot.isFinished())
                .findFirst();
    }

    public void setAnotherLot() {
        this.currentLot = getAnotherLot()
                .orElseThrow(NoLotsException::new);
    }
}
