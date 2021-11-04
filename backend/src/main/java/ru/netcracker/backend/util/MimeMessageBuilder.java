package ru.netcracker.backend.util;

import com.sun.istack.NotNull;
import lombok.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.netcracker.backend.model.user.User;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class MimeMessageBuilder {
    private JavaMailSender mailSender;
    private String fromAddress;
    private String toAddress;
    private String senderName;
    private String subject;
    private String content;
    private String username;
    private String methodURL;

    public MimeMessageBuilder(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public MimeMessageBuilder setFromAddress(@NonNull String fromAddress) {
        this.fromAddress = fromAddress;
        return this;
    }

    public MimeMessageBuilder setSenderName(@NonNull String senderName) {
        this.senderName = senderName;
        return this;
    }

    public MimeMessageBuilder setSubject(@NonNull String subject) {
        this.subject = subject;
        return this;
    }

    public MimeMessageBuilder setContent(@NonNull String content) {
        this.content = content;
        return this;
    }

    public MimeMessageBuilder setTo(@NonNull User user) {
        this.toAddress = user.getEmail();
        this.username = user.getUsername();
        return this;
    }

    public MimeMessageBuilder setMethodURL(@NotNull String methodURL) {
        this.methodURL = methodURL;
        return this;
    }

    public MimeMessage build() throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", username);
        content = content.replace("[[URL]]", methodURL);

        helper.setText(content, true);
        return message;
    }

}
