package ru.netcracker.backend.Models;

import lombok.Getter;
import lombok.Setter;
import ru.netcracker.backend.Models.User.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="tags")
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

}
