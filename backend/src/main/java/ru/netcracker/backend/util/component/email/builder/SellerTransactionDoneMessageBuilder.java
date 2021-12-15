package ru.netcracker.backend.util.component.email.builder;

import ru.netcracker.backend.model.entity.Transaction;
import ru.netcracker.backend.util.component.email.Email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class SellerTransactionDoneMessageBuilder extends EmailMessageBuilder{
    private final Transaction transaction;
    public SellerTransactionDoneMessageBuilder(Email email, Transaction transaction) {
        super(email);
        this.transaction = transaction;
    }

    @Override
    public MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        return buildMimeMessage()
                .setSubject("Transaction completed.")
                .setContent(generateContent("Congratulations on completing the transfer of the lot. Your earnings: " + transaction.getAmount() + "$."))
                .build();
    }
}
