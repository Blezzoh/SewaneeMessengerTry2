package com.danielevans.email;

import com.google.api.services.gmail.model.Thread;

import java.io.IOException;

/**
 * Created by daniel on 6/3/16.
 * @author Daniel Evans
 */

public class FullThread {

    private Authenticator auth;

    private Thread thread;

    public FullThread(Authenticator auth, Thread thread) throws IOException {

        this.auth = auth;
        this.thread = getFullThreadInstance(thread);
    }



    public FullThread(Inbox inbox, Thread thread) throws IOException {


        this(inbox.getAuth(), thread);
    }

    public Thread getFullThreadInstance(Thread thread) throws IOException {
        return auth.service.users().threads().get(auth.userId, thread.getId()).execute();
    }
}
