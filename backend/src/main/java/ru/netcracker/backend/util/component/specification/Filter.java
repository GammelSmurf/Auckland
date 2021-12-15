package ru.netcracker.backend.util.component.specification;

import lombok.Data;

import java.util.List;

@Data
public class Filter {
    private final String property;
    private final Operation operation;
    private final List<String> values;
    private final boolean orPredicate = true;
}
