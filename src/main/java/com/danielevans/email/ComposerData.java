package com.danielevans.email;

import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import messenger_interface.Composer;
import messenger_interface.Emailer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * Created by iradu_000 on 8/2/2016.
 * Class that holds data while of a Composer
 */
public class ComposerData {
    private String emailAddress, subject, cc, body;
    private int composerDataId;
    private Emailer emailer;

    public ComposerData(Composer composer, Emailer emailer) {
        this.emailer = emailer;
        this.emailAddress = composer.getEmailAddress().getText();
        this.subject= composer.getSubject().getText();
        this.cc = composer.getCc().getText();
        this.body = composer.getBodyText().getText();
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getBody() {
        return body;
    }

    public String getCc() {
        return cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public void setBody(String body) {
        this.body = body;
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
    public Draft createDraft()
            throws MessagingException, IOException {

        MimeMessage mimeMessage = emailer.composeMessage(emailAddress, subject, body);
        Message message = Inbox.createMessageWithEmail(mimeMessage);
        Draft draft = new Draft();
        draft.setMessage(message);
        draft = emailer.getAuth().service.users()
                .drafts().create(emailer.getAuth().userId, draft).execute();

        System.out.println("draft id: " + draft.getId());
        System.out.println(draft.toPrettyString());
        return draft;
    }

    // TODO: try to send multiple times, if fails notify user
    // TODO: validate information in data fields
    public void sendEmail() {
        MimeMessage mimeMessage = null;
        try {
            mimeMessage = emailer.composeMessage(emailAddress, subject, body);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            emailer.sendMessage(mimeMessage);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
