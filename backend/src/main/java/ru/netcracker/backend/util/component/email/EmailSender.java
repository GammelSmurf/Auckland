package ru.netcracker.backend.util.component.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.model.entity.Transaction;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.util.component.email.builder.*;

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

    public void createAndSendEmail(EmailLevel level, User to, String siteUrl, Object transferObject) throws MessagingException, UnsupportedEncodingException {
        javaMailSender.send(createMimeMessage(level, to, siteUrl, transferObject));
    }

    public void createAndSendVerificationEmail(User to, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.VERIFICATION, to, siteUrl, null);
    }

    public void createAndSendGeneratedPasswordEmail(User to, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.GENERATE_PASSWORD, to, siteUrl, null);
    }

    public void createAndSendLotWonEmail(User to, Lot lot) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.LOT_WON, to, null, lot);
    }

    public void createAndSendLotSoldEmail(User to, Lot lot) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.LOT_SOLD, to, null, lot);
    }

    public void createAndSendBuyerTransactionDoneEmail(User to) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.BUYER_TRANSACTION_DONE, to, null, null);
    }

    public void createAndSendSellerTransactionDoneEmail(User to, Transaction transaction) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.SELLER_TRANSACTION_DONE, to, null, transaction);
    }

    public void createAndSendTransferExpiredEmail(User to) throws MessagingException, UnsupportedEncodingException {
        createAndSendEmail(EmailLevel.TRANSACTION_EXPIRED, to, null, null);
    }

    private MimeMessage createMimeMessage(EmailLevel level, User to, String siteUrl, Object transferObject) throws MessagingException, UnsupportedEncodingException {
        return createMessageBuilder(level, to, siteUrl, transferObject).build();
    }

    private EmailMessageBuilder createMessageBuilder(EmailLevel level, User to, String siteUrl, Object transferObject) {
        switch (level) {
            case VERIFICATION:
                return new VerificationEmailMessageBuilder(getEmail(to), siteUrl);
            case GENERATE_PASSWORD:
                return new GeneratePasswordEmailMessageBuilder(getEmail(to), siteUrl);
            case LOT_WON:
                return new LotWonEmailMessageBuilder(getEmail(to), (Lot) transferObject);
            case LOT_SOLD:
                return new LotSoldEmailMessageBuilder(getEmail(to), (Lot) transferObject);
            case BUYER_TRANSACTION_DONE:
                return new BuyerTransactionDoneMessageBuilder(getEmail(to));
            case SELLER_TRANSACTION_DONE:
                return new SellerTransactionDoneMessageBuilder(getEmail(to), (Transaction) transferObject);
            case TRANSACTION_EXPIRED:
                return new TransactionExpirationMessageBuilder(getEmail(to));
            default:
                throw new IllegalArgumentException("Email level is not correct");
        }
    }

    private Email getEmail(User to) {
        return new Email(javaMailSender, fromAddress, senderName, to);
    }
}
