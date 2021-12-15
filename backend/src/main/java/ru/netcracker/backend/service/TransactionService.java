package ru.netcracker.backend.service;

public interface TransactionService {
    void deleteTransactionIfExpired();
}
