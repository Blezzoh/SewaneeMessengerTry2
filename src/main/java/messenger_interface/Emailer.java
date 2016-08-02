package messenger_interface;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * Created by daniel on 8/2/16.
 *
 * @author Daniel Evans
 */
public interface Emailer {

    public MimeMessage composeMessage(Session session, String to,
                                      String subject, String message)
            throws MessagingException;

    public void sendMessage(MimeMessage email)
            throws MessagingException, IOException;
}
