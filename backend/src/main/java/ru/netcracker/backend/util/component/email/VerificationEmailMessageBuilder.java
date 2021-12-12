package ru.netcracker.backend.util.component.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class VerificationEmailMessageBuilder extends EmailMessageBuilder {
    private final String siteUrl;

    public VerificationEmailMessageBuilder(Email email, String siteUrl) {
        super(email);
        this.siteUrl = siteUrl;
    }

    @Override
    public MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        return buildMimeMessage()
                .setSubject("Please verify your registration")
                .setMethodURL(String.format(
                        "%s/verify?username=%s&code=%s",
                        siteUrl, getEmail().getTo().getUsername(), getEmail().getTo().getVerificationCode()))
                .setContent(generateContent(
                        "Please click the link below to verify your registration:<br>"
                                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"))
                .build();
    }
}
