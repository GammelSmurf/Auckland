package ru.netcracker.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.entity.Transaction;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.repository.TransactionRepository;
import ru.netcracker.backend.service.TransactionService;
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
    private final EmailSender emailSender;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, LotRepository lotRepository, EmailSender emailSender) {
        this.transactionRepository = transactionRepository;
        this.lotRepository = lotRepository;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public void deleteTransactionIfExpired() {
        for (Transaction tx : transactionRepository.findAll()) {
            if (isDateTimeEqualOrMoreThan6Months(tx.getDateTime())) {
                try {
                    tx.getLot().setCanceled(true);
                    lotRepository.save(tx.getLot());
                    transactionRepository.delete(tx);

                    emailSender.createAndSendTransferExpiredEmail(tx.getAuctionCreator());
                    emailSender.createAndSendTransferExpiredEmail(tx.getBuyer());
                } catch (MessagingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isDateTimeEqualOrMoreThan6Months(LocalDateTime dateTime) {
        return ChronoUnit.MONTHS.between(dateTime, LocalDateTime.now()) >= 6;
    }
}
