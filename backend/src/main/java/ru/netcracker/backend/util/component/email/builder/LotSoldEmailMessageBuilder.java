package ru.netcracker.backend.util.component.email.builder;

import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.util.component.email.Email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class LotSoldEmailMessageBuilder extends EmailMessageBuilder{
    private final Lot lot;
    public LotSoldEmailMessageBuilder(Email email, Lot lot) {
        super(email);
        this.lot = lot;
    }

    @Override
    public MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        return buildMimeMessage()
                .setSubject("Lot Sold.")
                .setContent(generateContent("Congratulations, you have sold the lot: " + lot.getName() + "." +
                        "The winner of the lot should contact you through your contact details." +
                        "To make a transaction, after the transfer of the lot, confirm this in your profile."
                ))
                .build();
    }
}
