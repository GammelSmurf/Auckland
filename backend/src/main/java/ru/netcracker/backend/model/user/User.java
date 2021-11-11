package ru.netcracker.backend.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.utility.RandomString;
import ru.netcracker.backend.model.Bet;
import ru.netcracker.backend.model.auction.Auction;

import javax.persistence.*;
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
    private Boolean isBanned;

    private long currency;

    private String verificationCode;
    private String restoreCode;
    private boolean enabled;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        firstName = "";
        secondName = "";
        about = "";
        isBanned = false;
        verificationCode = RandomString.make(64);
        enabled = false;
    }

    @ElementCollection(targetClass = ERole.class, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<ERole> roles = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private Bet bet;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Auction> subscribes = new HashSet<>(0);
}
