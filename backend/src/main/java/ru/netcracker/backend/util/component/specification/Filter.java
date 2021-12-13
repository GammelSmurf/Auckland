package ru.netcracker.backend.util.component.specification;

import lombok.Data;

@Data
public class Filter {
    private final String property;
    private final Operation operation;
    private final String value;
}
