package com.danielevans.email;

import messenger_interface.Composer;

/**
 * Created by iradu_000 on 8/2/2016.
 * Class that holds data while of a Composer
 */
public class ComposerData {
    private String emailAddress, subject, cc, body;
    private int composerDataId;

    public ComposerData(Composer composer){
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
}
