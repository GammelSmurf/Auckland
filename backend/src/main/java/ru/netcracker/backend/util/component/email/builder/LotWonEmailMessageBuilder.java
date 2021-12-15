package ru.netcracker.backend.util.component.email.builder;

import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.util.component.email.Email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class LotWonEmailMessageBuilder extends EmailMessageBuilder {
    private final Lot lot;

    public LotWonEmailMessageBuilder(Email email, Lot lot) {
        super(email);
        this.lot = lot;
    }

    @Override
    public MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        return buildMimeMessage()
                .setSubject("Lot Won.")
                .setContent(generateContent("Congratulations, you have won the lot: " + lot.getName() + "." +
                        "Please contact the auctioneer via the contact details to discuss the transfer of the lot" + lot.getAuction().getCreator().getEmail() + "." +
                        "To make a transaction, after the transfer of the lot, confirm this in your profile."
                ))
                .build();
    }
}
