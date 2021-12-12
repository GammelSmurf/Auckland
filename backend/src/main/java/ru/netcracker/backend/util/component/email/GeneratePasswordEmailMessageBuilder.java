package ru.netcracker.backend.util.component.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class GeneratePasswordEmailMessageBuilder extends EmailMessageBuilder {
    private final String siteUrl;
    public GeneratePasswordEmailMessageBuilder(Email email, String siteUrl) {
        super(email);
        this.siteUrl = siteUrl;
    }

    @Override
    MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        return buildMimeMessage()
                .setSubject("Password recover")
                .setMethodURL(String.format(
                        "%s/restore?username=%s&code=%s",
                        siteUrl, getEmail().getTo().getUsername(), getEmail().getTo().getRestoreCode()))
                .setContent(generateContent(
                        "Please click the link below to restore your password:<br>"
                                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"))
                .build();
    }
}
