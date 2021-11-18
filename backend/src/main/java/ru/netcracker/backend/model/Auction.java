package ru.netcracker.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private LocalTime lotDuration;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.DRAFT;

    private LocalTime boostTime;
    private Integer usersLimit;

    @OneToOne(mappedBy = "auction")
    @JsonBackReference
    private Bet bet;

    @OneToOne(mappedBy = "auction")
    @JsonBackReference
    private Log log;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "auctions_tags",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "ratings",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> userLikes = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> subscribers = new HashSet<>();

    public int getLikesCount() {
        return getUserLikes().size();
    }

    public int getSubscribersCount() {
        return getSubscribers().size();
    }
}
