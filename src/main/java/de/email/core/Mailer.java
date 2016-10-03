package de.email.core;

import SophiaMessenger.Models.ComposerData;
import com.google.api.services.gmail.model.Message;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Optional;

import static de.email.core.Inbox.createMessageWithEmail;
import static de.email.core.Inbox.getSessionWithDefaultProps;

/**
 * Created by daniel on 8/22/16.
 *
 * @author Daniel Evans
 */
public class Mailer {

    private static final String SUBJECT_EMPTY =
            "The subject field is empty. Do you still want to" +
                    "send the message?";
    private ComposerData data;
    private Authenticator auth;

    public Mailer(ComposerData data, Authenticator auth) {
        Preconditions.objectNotNull(data, "ComposerData is null, data = " + data);
        Preconditions.objectNotNull(auth, "Auth is null, auth = " + auth);
        this.data = data;
        this.auth = auth;

    }

    /**
     * Send an email from the user's mailbox to its recipient.
     * <p>
     * can be used to indicate the authenticated user.
     *
     * @return true if the message sent successfully and we should close the composer box
     * false if there was some problem or the user cancelled the send
     * @throws MessagingException if the email address to send to is bad
     * @throws IOException        if the the underlying implementation encounters an
     *                            when writing the the output stream for the email
     */
    public boolean sendMessage()
            throws MessagingException, IOException {

        Message message = null;
        Object obj = composeMessage(data.getEmailAddress(), data.getCc(), data.getSubject(), data.getBody());
        // user cancelled sending the message
        if (obj == null) return false;
        if (obj instanceof MimeMessage) {
            message = createMessageWithEmail((MimeMessage) obj);
        } else {// the email address was bad
            // only other thing return from composeMessage is an exception
            // so throw new exception with message to user
            throw (MessagingException) obj;
        }
        assert message != null;
        auth.service.users().messages().send(auth.userId, message).execute();
        // true means the message was sent successfully,
        // and we should close the composer box
        return true;
    }

    /**
     * @param to      email address of receiver
     * @param subject subject of message
     * @param message body of the message
     * @return composed MimeMessage
     * @throws MessagingException
     */
    private Object composeMessage(String to,
                                  String Cc,
                                  String subject,
                                  String message) {

        MimeMessage m = new MimeMessage(getSessionWithDefaultProps());
        try {
            // this.auth.userId should always be a valid address
            m.setFrom(new InternetAddress(this.auth.userId));
        } catch (MessagingException e) {
            // user has invalid user address ???
            e.printStackTrace();
        }
        // TODO: if CC/BCC contains an email address, iterate through the
        // TODO: email addresses using this method
        try {
            String[] toSplit = to.split(" ");
            for (String toAddress : toSplit) {
                if (!toAddress.contains("@") || !toAddress.contains("."))
                    throw new MessagingException(
                            "The address \"" + toAddress + "\" in the \"To\" was " +
                                    "not recognized. Make sure it was not malformed."
                    );
                InternetAddress[] address = InternetAddress.parse(toAddress);
                m.addRecipients(MimeMessage.RecipientType.TO,
                        address);
            }
            if (!Cc.equals("")) {
                String[] splitCc = Cc.split(" ");
                for (String cCAddress : splitCc) {
                    if (!cCAddress.contains("@") || !cCAddress.contains("."))
                        throw new MessagingException(
                                "The address \"" + cCAddress + "\" in the \"Cc\" was " +
                                        "not recognized. Make sure it was not malformed."
                        );
                    m.addRecipients(MimeMessage.RecipientType.CC,
                            InternetAddress.parse(cCAddress));
                }
            }
        } catch (MessagingException e) {
            return e;
        }
        try {
            subject = subject.replace("\n", ""); // make subject valid
            if (subject.length() == 0) { // empty subject line
                Alert subjectEmptyAlert = new Alert(Alert.AlertType.CONFIRMATION);
                subjectEmptyAlert.setTitle("Subject field is empty");
                subjectEmptyAlert.setHeaderText(null);
                subjectEmptyAlert.setContentText(SUBJECT_EMPTY);
                Optional<ButtonType> result = subjectEmptyAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    m.setSubject(subject);
                } else {
                    // user chose CANCEL or closed the dialog b/c subject empty
                    return null;
                }
            } else { // subject has text 
                m.setSubject(subject);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            m.setContent(message, "text/html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return m;
    }
}
