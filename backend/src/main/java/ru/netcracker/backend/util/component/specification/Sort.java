package ru.netcracker.backend.util.component.specification;

import lombok.Data;
import ru.netcracker.backend.util.enumiration.Direction;

@Data
public class Sort {
    private final Direction direction;
    private final String property;
}
