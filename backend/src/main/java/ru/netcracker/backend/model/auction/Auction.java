package ru.netcracker.backend.model.auction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.model.AuctionLog;
import ru.netcracker.backend.model.AuctionProcess;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.model.Tag;
import ru.netcracker.backend.model.user.User;

import javax.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
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
    private Timestamp beginDate;
    private Time lotDuration;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    private Time boostTime;
    private Integer usersLimit;

    public Auction() {
        status = AuctionStatus.DRAFT;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "auction",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Lot> lots = new HashSet<>(0);

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

    @OneToOne(mappedBy = "auction")
    private AuctionProcess auctionProcess;

    @OneToOne(mappedBy = "auction")
    private AuctionLog auctionLog;

    public int getLikesCount() {
        return getUserLikes().size();
    }

    public int getSubscribersCount() {
        return getSubscribers().size();
    }
}
