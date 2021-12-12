package ru.netcracker.backend.util.component.email;

import lombok.Data;
import ru.netcracker.backend.util.EmailUtil;
import ru.netcracker.backend.util.builder.MimeMessageBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Data
public abstract class EmailMessageBuilder {
    private final Email email;

    public EmailMessageBuilder(Email email) {
        this.email = email;
    }

    abstract MimeMessage build() throws MessagingException, UnsupportedEncodingException;

    public String generateContent(String content) {
        return EmailUtil.HELLO_MSG_TEMPLATE + content + String.format(EmailUtil.BYE_MSG_TEMPLATE, email.getSenderName());
    }

    public MimeMessageBuilder buildMimeMessage() {
        return new MimeMessageBuilder(email.getJavaMailSender())
                .setFromAddress(email.getFromAddress())
                .setSenderName(email.getSenderName())
                .setTo(email.getTo());
    }
}
