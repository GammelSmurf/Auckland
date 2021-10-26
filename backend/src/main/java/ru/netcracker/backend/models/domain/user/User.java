package ru.netcracker.backend.models.domain.user;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.utility.RandomString;
import ru.netcracker.backend.models.domain.Auction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String name;
    private String secondName;
    private String about;
    @Enumerated(EnumType.STRING)
    private EStatus status;
    private Boolean isBanned;

    private String verificationCode;
    private boolean enabled;

    public User(){}

    public User(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
        name = "";
        secondName = "";
        about = "";
        status = EStatus.NOT_CONFIRMED;
        isBanned = false;
        verificationCode = RandomString.make(64);
        enabled = false;
    }

    @ElementCollection(targetClass=ERole.class, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<ERole> roles= new HashSet<>();


    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Auction> subscribes= new HashSet<>(0);
}
