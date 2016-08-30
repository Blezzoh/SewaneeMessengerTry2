package SophiaMessenger.Models;

import SophiaMessenger.Views.Composer;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by iradu_000 on 8/2/2016.
 * Class that holds data while of a Composer
 */
public class ComposerData {
    private String emailAddress, subject, cc, body;
    private int composerDataId;

    public ComposerData(Composer composer) {
        this.emailAddress = composer.getEmailAddress().getText();
        this.subject= composer.getSubject().getText();
        this.cc = composer.getCc().getText();
        this.body = composer.getBodyText().getText();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getComposerDataId() {
        return composerDataId;
    }

    public void setComposerDataId(int composerDataId) {
        this.composerDataId = composerDataId;
    }

    /**
     * Create draft email.
     *
     * @return Created Draft.
     * @throws MessagingException
     * @throws IOException        probably there is no Internet
     */
    // TODO: job for the controller not for the composerData
/*    public Draft createDraft()
            throws MessagingException, IOException {

        MimeMessage mimeMessage = emailSender.composeMessage(emailAddress, subject, body);
        DBMessage message = Inbox.createMessageWithEmail(mimeMessage);
        Draft draft = new Draft();
        draft.setMessage(message);
        draft = emailSender.getAuth().service.users()
                .drafts().create(emailSender.getAuth().userId, draft).execute();

        System.out.println("draft id: " + draft.getId());
        System.out.println(draft.toPrettyString());
        return draft;
    }*/

}
