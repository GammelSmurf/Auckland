package ru.netcracker.backend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.model.Transaction;
import ru.netcracker.backend.model.TransactionStatus;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByTransactionStatus(TransactionStatus txStatus);
}
