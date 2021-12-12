package ru.netcracker.backend.util.component.email;

import lombok.Data;
import org.springframework.mail.javamail.JavaMailSender;
import ru.netcracker.backend.model.entity.User;

@Data
public class Email {
    private final JavaMailSender javaMailSender;
    private final String fromAddress;
    private final String senderName;
    private final User to;
}
