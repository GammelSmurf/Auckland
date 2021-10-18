package ru.netcracker.backend.Models;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.Models.User.Role;
import ru.netcracker.backend.Models.User.User;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    private Date begin_date;

    private Time lot_duration;
    private Time boost_time;

    private Integer users_limit;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "auction_tags",
            joinColumns = @JoinColumn(name = "auction_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id") )
    private Set<Tag> tags= new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ratings",
            joinColumns = @JoinColumn(name = "auction_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id") )
    private Set<User> user_likes= new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "subsriptions",
            joinColumns = @JoinColumn(name = "auction_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id") )
    private Set<User> subscribers= new HashSet<>();

}
