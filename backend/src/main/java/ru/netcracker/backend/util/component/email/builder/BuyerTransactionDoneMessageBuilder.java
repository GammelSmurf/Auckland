package ru.netcracker.backend.util.component.email.builder;

import ru.netcracker.backend.util.component.email.Email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class BuyerTransactionDoneMessageBuilder extends EmailMessageBuilder{
    public BuyerTransactionDoneMessageBuilder(Email email) {
        super(email);
    }

    @Override
    public MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        return buildMimeMessage()
                .setSubject("Transaction completed.")
                .setContent(generateContent("Congratulations on completing the receipt of the lot."))
                .build();
    }
}
