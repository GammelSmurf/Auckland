package ru.netcracker.backend.util.component.email.builder;

import ru.netcracker.backend.util.component.email.Email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class TransactionExpirationMessageBuilder extends EmailMessageBuilder{
    public TransactionExpirationMessageBuilder(Email email) {
        super(email);
    }

    @Override
    public MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        return buildMimeMessage()
                .setSubject("Transaction expired.")
                .setContent(generateContent("Sorry, but your transaction has not been confirmed for 6 months. In this regard, it is canceled."))
                .build();
    }
}
