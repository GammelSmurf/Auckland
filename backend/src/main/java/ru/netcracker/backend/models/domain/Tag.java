package ru.netcracker.backend.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="tags")
@Getter
@Setter
public class Tag {
    @Id
    @Column(name="tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
