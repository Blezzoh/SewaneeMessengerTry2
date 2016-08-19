package de.email.interfaces;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * Created by daniel on 8/2/16.
 *
 * @author Daniel Evans
 */
public interface EmailSender extends Auth {

    MimeMessage composeMessage(String to,
                               String subject, String message)
            throws MessagingException;

    void sendMessage(MimeMessage email)
            throws MessagingException, IOException;
}
