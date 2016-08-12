package com.danielevans.email;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 6/3/16.
 * @author Daniel Evans
 */

public class FullThread implements Auth {

    private Authenticator auth;

    private Thread thread;

    public FullThread(Authenticator auth, Thread thread) {

        this.auth = auth;
        try {
            this.thread = getFullThreadInstance(thread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FullThread(Inbox inbox, Thread thread){

        this(inbox.getAuth(), thread);
    }

    public List<FullMessage> getFullMessages(Inbox inbox) {
        List<FullMessage> fms = new ArrayList<>(thread.getMessages().size());
        for (int i = 0; i < thread.getMessages().size(); i++) {
            try {
                fms.add(new FullMessage(inbox, thread.getMessages().get(i)));
            } catch (IOException e) { e.printStackTrace(); }
        }
        return fms;
    }

    /**
     * @param thread       thread to take the message from
     * @param whichMessage number of the message in the thread, 0 = first message
     *                     1 = second message, etc
     * @return the body of the specified message in the thread, or the body
     * of the last message if whichMessage greater than thread.getMessages().size()
     * @throws IOException
     */
    public String getBodyOfMessageInThread(Inbox inbox, Thread thread, int whichMessage)
            throws IOException {

        if (thread == null) throw new NullPointerException("message is null");

        FullThread t = new FullThread(inbox, thread);
        // if user requests a message that doesn't exist in thread (ex: only 2 messages
        // in the thread, user requests the third), then return the last message
        FullMessage fm;
        Message m;
        if (t.getMessages().size() <= whichMessage) {
            m = t.getMessages().get(t.getMessages().size() - 1);
            // return the last message in the thread
            fm = new FullMessage(inbox, m);
            return inbox.decodeString(fm.getBodyBase64());
        }
        m = t.getMessages().get(whichMessage);
        fm = new FullMessage(inbox, m);
        // return the message queried for
        return Inbox.decodeString(fm.getBodyBase64());
    }
    public List<Message> getMessages() {
        return thread.getMessages();
    }

    /**
     *
     * @param thread any thread
     * @return a thread with full payload
     * @throws IOException
     */
    public Thread getFullThreadInstance(Thread thread) throws IOException {
        Preconditions.objectNotNull(thread, "thread is null");
        return auth.service.users().threads().get(auth.userId, thread.getId()).execute();
    }

    /**
     * Immediately and permanently deletes the specified thread. This operation cannot
     * be undone. Prefer threads.trash instead.
     * @param thread the Thread to delete.
     */
    public void deleteThread(Thread thread) {
        Preconditions.objectNotNull(thread, "threadId cannot be null");
        try {
            auth.service.users().threads().delete(auth.userId, thread.getId());
            System.out.println("Thread with id: " + thread.getId()
                                        + " deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error deleting thread with id: " + thread.getId());
        }
    }

    /**
     * Remove the specified thread from Trash.
     * @param thread The thread to remove from Trash.
     */
    public void untrashThread(Thread thread) {
        try {
            auth.service.users()
                    .threads().untrash(auth.userId, thread.getId()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Thread with id: " + thread.getId()
                                            + " has been untrashed.");
    }

    public void untrashThread() {
        untrashThread(thread);
    }

    public void deleteThread() {
        deleteThread(thread);
    }

    /**
     * Trash the specified thread.
     * @param thread the Thread to trash.
     */
    public void trashThread(Thread thread) {
        try {
            auth.service.users().threads().trash(auth.userId, thread.getId()).execute();
            System.out.println("Thread with id: " + thread.getId()
                                                    + " has been trashed.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error trashing thread with id: "
                                                            + thread.getId());
        }
    }
    public void trashThread() {
        trashThread(thread);
    }

    public String getId() {
        return thread.getId();
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

    @Override
    public Authenticator getAuth() {
        return auth;
    }
}
