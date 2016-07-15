package com.danielevans.email;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import java.io.IOException;
import java.util.List;

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

    public List<Message> getMessages() {
        return thread.getMessages();
    }

    public Thread getFullThreadInstance(Thread thread) throws IOException {
        return auth.service.users().threads().get(auth.userId, thread.getId()).execute();
    }

    /**
     * @param thread useful information about thread
     * @throws IOException
     */
    public void getThreadInfo(Thread thread) throws IOException {
        Thread t = auth.service.users().threads().get(auth.userId, thread.getId()).execute();
        System.out.println("No. of messages in this thread: " + t.getMessages().size());
        System.out.println(t.toPrettyString());
    }
}
