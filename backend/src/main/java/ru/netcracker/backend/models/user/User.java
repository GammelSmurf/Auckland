package ru.netcracker.backend.models.user;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.models.Auction;

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
    private String status;
    private Boolean isBanned;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id") ,
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles= new HashSet<>();


    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<Auction> subscribes= new HashSet<>(0);
}