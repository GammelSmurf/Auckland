package ru.netcracker.backend.util.component.specification;

public enum Direction {
    ASC,
    DESC;

    public boolean isAscending() {
        return this.equals(ASC);
    }

    public boolean isDescending() {
        return this.equals(DESC);
    }
}
