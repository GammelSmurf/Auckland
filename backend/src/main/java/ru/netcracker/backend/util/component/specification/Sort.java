package ru.netcracker.backend.util.component.specification;

import lombok.Data;

@Data
public class Sort {
    private final Direction direction;
    private final String property;
}
