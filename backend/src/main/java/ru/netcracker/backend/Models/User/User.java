package ru.netcracker.backend.Models.User;


import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.Models.Auction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String second_name;
    private String about;
    private String status;
    private Boolean is_banned;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id") )
    private Set<Role> roles= new HashSet<>();


    @ManyToMany(mappedBy = "subscribers",fetch = FetchType.LAZY)
    private Set<Auction> subscribe= new HashSet<>(0);
}
