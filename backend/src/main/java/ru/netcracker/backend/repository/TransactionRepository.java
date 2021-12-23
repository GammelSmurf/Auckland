package ru.netcracker.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.model.entity.Transaction;
import ru.netcracker.backend.model.entity.User;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByLot(Lot lot);
    
    Optional<Transaction> findByBuyer_UsernameAndLot_Id(String username, Long lotId);
}
