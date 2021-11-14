package ru.netcracker.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.utility.RandomString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
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

    private BigDecimal currency = new BigDecimal(0);

    private String verificationCode = RandomString.make(64);
    private String restoreCode;
    private boolean isBanned = false;
    private boolean enabled = false;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @OneToOne(mappedBy = "user")
    private Bet bet;

    @OneToOne(mappedBy = "winner")
    private Lot lot;

    @OneToMany(
            mappedBy = "creator",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Auction> ownAuctions = new HashSet<>(0);

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "subscribers")
    private Set<Auction> subscribedAuctions = new HashSet<>();
}
