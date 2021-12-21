package ru.netcracker.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.entity.Transaction;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.repository.TransactionRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.service.TransactionService;
import ru.netcracker.backend.service.UserService;
import ru.netcracker.backend.util.component.email.EmailSender;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final LotRepository lotRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final UserService userService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  LotRepository lotRepository,
                                  UserRepository userRepository,
                                  EmailSender emailSender,
                                  UserService userService) {
        this.transactionRepository = transactionRepository;
        this.lotRepository = lotRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void deleteTransactionIfExpired() {
        for (Transaction tx : transactionRepository.findAll()) {
            if (isDateTimeEqualOrMoreThan6Months(tx.getDateTime())) {
                tx.getLot().setCanceled(true);
                tx.getBuyer().addMoney(tx.getAmount());

                userRepository.save(tx.getBuyer());
                lotRepository.save(tx.getLot());

                userService.sendMoneyToWsByUser(tx.getBuyer());
                transactionRepository.delete(tx);
                sendTransactionExpiredEmails(tx);
            }
        }
    }

    private void sendTransactionExpiredEmails(Transaction tx) {
        try {
            emailSender.createAndSendTransferExpiredEmail(tx.getAuctionCreator());
            emailSender.createAndSendTransferExpiredEmail(tx.getBuyer());
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private boolean isDateTimeEqualOrMoreThan6Months(LocalDateTime dateTime) {
        return ChronoUnit.MONTHS.between(dateTime, LocalDateTime.now()) >= 6;
    }
}
