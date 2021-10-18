package ru.netcracker.backend.models;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.models.user.User;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
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
    private Date begin_date;
    private Time lot_duration;
    private Time boost_time;
    private Integer users_limit;

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
    private Set<User> user_likes= new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "auction_id") ,
            inverseJoinColumns = @JoinColumn(name = "user_id") )
    private Set<User> subscribers= new HashSet<>();

}
