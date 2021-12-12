package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.model.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
