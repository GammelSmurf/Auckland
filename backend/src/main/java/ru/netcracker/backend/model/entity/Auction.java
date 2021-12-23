package ru.netcracker.backend.model.entity;

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

    private LocalDateTime beginDateTime;
    private LocalDateTime endDateTime;
    private LocalTime lotDurationTime;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.DRAFT;

    private LocalTime extraTime;
    private Long usersCountLimit;

    private Long subscribedUsersCount = 0L;
    private Long userLikesCount = 0L;

    @OneToOne(mappedBy = "auction")
    @JsonBackReference
    private Bid currentBid;

    @OneToOne
    @JoinColumn(name = "current_lot_id")
    @JsonBackReference
    private Lot currentLot;

    @ManyToOne
    @JoinColumn(name = "creator_user_id", nullable = false)
    @JsonBackReference
    private User creator;

    @OneToMany(
            mappedBy = "auction",
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Lot> lots = new ArrayList<>(0);

    @OneToMany(mappedBy = "auction")
    @JsonBackReference
    private Set<Log> logs = new HashSet<>();

    @OneToMany(
            mappedBy = "auction",
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Tag> tags = new HashSet<>();

    @JsonBackReference
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "auction_categories",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonBackReference
    private Set<User> subscribedUsers = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "ratings",
            joinColumns = @JoinColumn(name = "auction_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> usersWhoLiked = new HashSet<>();

    @OneToMany(mappedBy = "auction")
    @JsonBackReference
    private Set<Notification> notifications = new HashSet<>();

    public void copyMainParamsFrom(Auction auction) {
        setName(auction.getName());
        setDescription(auction.getDescription());
        setUsersCountLimit(auction.getUsersCountLimit());
        setBeginDateTime(auction.getBeginDateTime());
        setLotDurationTime(auction.getLotDurationTime());
        setExtraTime(auction.getExtraTime());
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

    public void setWaitingStatus() {
        setStatus(AuctionStatus.WAITING);
    }

    public void setRunningStatus() {
        setStatus(AuctionStatus.RUNNING);
    }

    public void setFinishedStatus() {
        setStatus(AuctionStatus.FINISHED);
    }

    public void incUsersCount() {
        this.subscribedUsersCount++;
    }

    public void addUserWhoLikedAndIncreaseLikesCount(User user) {
        getUsersWhoLiked().add(user);
        incLikes();
    }

    public void removeUserWhoLikedAndDecreaseLikesCount(User user) {
        getUsersWhoLiked().remove(user);
        decLikes();
    }

    public void incLikes() {
        this.userLikesCount++;
    }

    public void decLikes() {
        this.userLikesCount--;
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

    public void addCategory(Category category) {
        category.getAuctions().add(this);
        getCategories().add(category);
    }

    public void removeCategory(Category category) {
        category.getAuctions().remove(this);
        getCategories().remove(category);
    }
}
