package ru.netcracker.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.utility.RandomString;
import ru.netcracker.backend.model.requests.UserRequest;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String secondName;
    private String about;

    private BigDecimal money = new BigDecimal(10000);

    private String verificationCode = RandomString.make(64);
    private String restoreCode;
    private boolean banned = false;
    private boolean enabled = false;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private Set<Bid> bid;

    @OneToMany(
            mappedBy = "winner",
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Lot> lots = new ArrayList<>(0);

    @OneToMany(
            mappedBy = "creator",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Auction> ownAuctions = new HashSet<>(0);

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<UserRole> userRoles = new HashSet<>();

    @ManyToMany(mappedBy = "subscribedUsers")
    private Set<Auction> subscribedAuctions = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private Set<Notification> notifications = new HashSet<>();

    public void subtractMoney(BigDecimal currency) {
        this.money = this.money.subtract(currency);
    }

    public void addMoney(BigDecimal currency) {
        this.money = this.money.add(currency);
    }

    public void subscribeToAuction(Auction auction) {
        auction.getSubscribedUsers().add(this);
        auction.incUsersCount();
        this.getSubscribedAuctions().add(auction);
    }

    public void copyMainParamsFrom(UserRequest userRequest) {
        setFirstName(userRequest.getFirstName());
        setSecondName(userRequest.getSecondName());
        setAbout(userRequest.getAbout());
    }
}
