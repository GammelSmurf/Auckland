package ru.netcracker.backend.util.enumiration;

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
