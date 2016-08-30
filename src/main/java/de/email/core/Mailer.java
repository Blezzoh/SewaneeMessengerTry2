package de.email.core;

import SophiaMessenger.Models.ComposerData;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static de.email.core.Inbox.createMessageWithEmail;
import static de.email.core.Inbox.getSessionWithDefaultProps;

/**
 * Created by daniel on 8/22/16.
 *
 * @author Daniel Evans
 */
public class Mailer {

    private ComposerData data;
    private Authenticator auth;

    public Mailer(ComposerData data, Authenticator auth) {
        Preconditions.objectNotNull(data, "ComposerData is null, data = " + data);
        Preconditions.objectNotNull(auth, "Auth is null, auth = " + auth);
        this.data = data;
        System.out.println(data.getBody());
        System.out.println(data.getSubject());
        System.out.println(data.getEmailAddress());
        this.auth = auth;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     * <p>
     * can be used to indicate the authenticated user.
     *
     * @throws MessagingException
     * @throws IOException
     */
    public void sendMessage()
            throws MessagingException, IOException {

        System.out.println("I can feel it now the rain has gone!");
        MimeMessage mimeMessage = composeMessage(data.getEmailAddress(), data.getSubject(), data.getBody());
        Message message = createMessageWithEmail(mimeMessage);
        message = auth.service.users().messages().send(auth.userId, message).execute();
    }

    /**
     * @param to      email address of receiver
     * @param subject subject of message
     * @param message body of the message
     * @return composed MimeMessage
     * @throws MessagingException
     */
    private MimeMessage composeMessage(String to,
                                       String subject,
                                       String message) throws MessagingException {

        MimeMessage m = new MimeMessage(getSessionWithDefaultProps());
        m.setFrom(new InternetAddress(this.auth.userId));

        // TODO: if CC/BCC contains an email address, iterate through the
        // TODO: email addresses using this method
        m.addRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
        m.setSubject(subject);
        m.setText(message);
        return m;
    }
}
