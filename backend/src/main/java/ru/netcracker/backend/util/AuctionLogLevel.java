package ru.netcracker.backend.util;

public enum AuctionLogLevel {
    AUCTION_BET("-Bet-"),
    AUCTION_STATUS_CHANGE("-Status change-"),
    DEBUG("-Debug-");

    private final String text;

    AuctionLogLevel(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
