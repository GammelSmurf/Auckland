package ru.netcracker.backend.util.component.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.util.enumiration.EmailLevel;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Component
public class EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${Auckland.mail.senderName}")
    private String senderName;

    @Autowired
    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void createAndSendEmail(EmailLevel level, User to, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        javaMailSender.send(createMimeMessage(level, to, siteUrl));
    }

    public void createAndSendVerificationEmail(User to, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.VERIFICATION, to, siteUrl);
    }

    public void createAndSendGeneratedPasswordEmail(User to, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.GENERATE_PASSWORD, to, siteUrl);
    }

    private MimeMessage createMimeMessage(EmailLevel level, User to, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        return createMessageBuilder(level, to, siteUrl).build();
    }

    private EmailMessageBuilder createMessageBuilder(EmailLevel level, User to, String siteUrl) {
        switch (level) {
            case VERIFICATION:
                return new VerificationEmailMessageBuilder(getEmail(to), siteUrl);
            case GENERATE_PASSWORD:
                return new GeneratePasswordEmailMessageBuilder(getEmail(to), siteUrl);
            default:
                throw new IllegalArgumentException("Email level is not correct");
        }
    }

    private Email getEmail(User to) {
        return new Email(javaMailSender, fromAddress, senderName, to);
    }
}
