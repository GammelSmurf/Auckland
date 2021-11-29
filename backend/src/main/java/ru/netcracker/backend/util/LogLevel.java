package ru.netcracker.backend.util;

public enum LogLevel {
    AUCTION_BET("-Bet-"),
    AUCTION_STATUS_CHANGE("-Status change-"),
    AUCTION_WINNER("-Winner-"),
    AUCTION_NO_WINNER("-No Winner-"),
    DEBUG("-Debug-");

    private final String text;

    LogLevel(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
