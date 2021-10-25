package ru.netcracker.backend.models.domain;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.models.domain.user.User;

import javax.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="auctions")
@Getter
@Setter
public class Auction {
    @Id
    @Column(name="auction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Timestamp beginDate;
    private Time lotDuration;

    private Time boostTime;
    private Integer usersLimit;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    private User user;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "auctions_tags",
            joinColumns = @JoinColumn(name = "auction_id") ,
            inverseJoinColumns = @JoinColumn(name = "tag_id") )
    private Set<Tag> tags= new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "ratings",
            joinColumns = @JoinColumn(name = "auction_id") ,
            inverseJoinColumns = @JoinColumn(name = "user_id") )
    private Set<User> userLikes= new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "auction_id") ,
            inverseJoinColumns = @JoinColumn(name = "user_id") )
    private Set<User> subscribers= new HashSet<>();

    public int getLikesCount() {
        return getUserLikes().size();
    }

    public int getSubscribersCount() {
        return getSubscribers().size();
    }
}
