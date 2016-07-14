package com.danielevans.email;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;

import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 6/3/16.
 */
public class FullMessage {

    private static final String DATE = "Date";
    private static final String DELIVERED_TO = "Delivered-To";
    private static final String FROM = "From";
    private static final String TO = "To";
    private static final String REPLY_TO = "Reply-To";
    private static final String SUBJECT = "Subject";
    private static final String LIST_UNSUBSCRIBE = "List-Unsubscribe";
    private static final String MAILING_LIST = "Mailing-List";

    private Message m;
    private Authenticator auth;


    // TODO: surround long returns with try/catch(NPE e)

    public FullMessage(Authenticator auth, Message message) throws IOException {
        this.auth = auth;
        this.m = getFullMessageMetaData(message);
    }

    public FullMessage(Inbox inbox, Message message) throws IOException {
        this(inbox.getAuth(), message);
    }

    /*    // basically a copy constructor because m is already a full message retrieved with get
     public FullMessage(Inbox inbox, Message m) throws IOException {
         // if payload field is null, this message was not retrieved with messages.get
         if(m.getPayload() == null) {
             throw new IllegalArgumentException("m (" + m + ") must be a message retrieved with get");
         }
         this.auth = inbox.getAuth();
         this.m = m;
     }
     */
    public Authenticator getAuth() {
        return auth;
    }
    /**
     * @return the message body in HTML
     * @throws IOException
     */
    public String getMessageAsHTML() throws IOException {
        String html = Inbox.decodeString(m.getPayload().getParts().get(1)
                .getBody().getData());

        if (html == null)
            try {
                html = getRawVersion(m).getRaw();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        return html;
    }


    /**
     * gets the message with containing info specified by fields
     *
     * @return a full instance version of the message passed
     */
    public Message getFullMessageMetaData(Message message) {
            try {
                // set fields
                return auth.service.users().messages()
                        .get(auth.userId, message.getId())
                        .setFormat("metadata")
                        .set("metadataIncludeHeaders", TO)
                        .set("metadataIncludeHeaders", FROM)
                        .set("metadataIncludeHeaders", DATE)
                        .set("metadataIncludeHeaders", SUBJECT)
                        .execute();
            } catch (IOException e) {
                System.out.println("CANNOT RETRIEVE THE MESSAGE");
                e.printStackTrace();

                return null;
            }
    }

    public String getSnippet() {
        return m.getSnippet();
    }

    /**
     * helper method that gets the message formatted with format=RAW
     *
     * @param message doesn't assume this is a full instance method
     * @return returns the entire raw version of the message
     */
    private Message getRawVersion(Message message) {
        try {
            return auth.service.users().messages().get(auth.userId, message.getId()).set("format", "RAW").execute();
        } catch (IOException e) {
            System.out.println("unable to retrieve raw version of the message");
            e.printStackTrace();

            return null;
        }
    }

    /**
     * helper method to get certain pieces of the message
     *
     * @param part the name of the header part
     * @return returns the value of the header designated by part
     */
    private String getHeaderPart(String part) {

        List<MessagePartHeader> headers = m.getPayload().getHeaders();
        m.getPayload().get("this");
        String retval = "";
        for (MessagePartHeader header : headers) {
//            System.out.println(header);
            if (header.getName().equals(part)) {
//                System.out.println(headers.get(i).getValue());
                retval = header.getValue();
            }
        }
//        System.out.println("\n------------------------------------------\n");
        // at this point, we know message is not found
        // so return empty string
        return retval;
    }
    /**
     * @return the text in the body of message
     */
    public String getMessageBody()
    {
        // note that this will throw a null pointer exception
        // if the message only contains html
        // that is m.getPayload.getParts will be null
        // TODO: Fix the above comments
        Message message = null;
        try {
            message = auth.service.users().messages()
                    .get(auth.userId, this.m.getId()).setFields("payload").execute();
            return Inbox.decodeString(message.getPayload().getParts().get(0).getBody().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getMessageBody(Message message) {
        // note that this will throw a null pointer exception
        // if the message only contains html
        // that is m.getPayload.getParts will be null
        // TODO: Fix the above comments
        return Inbox.decodeString(message.getPayload().getParts().get(0).getBody().getData());
    }
    /**
     *
     * @param thread thread to take the message from
     * @param whichMessage number of the message in the thread, 0 = first message
     *                     1 = second, etc
     * @return the body of the specified message in the thread, or the body
     * of the last message if whichMessage greater than thread.getMessages().size()
     * @throws IOException
     */
    public String getBodyOfMessageInThread(Thread thread, int whichMessage)
            throws IOException {

        if(thread == null) throw new NullPointerException("message is null");

        Thread t = getFullThreadInstance(thread);

        // if user requests a message that doesn't exist in thread (ex: only 2 messages
        // in the thread, user requests the third), then return the last message
        if(t.getMessages().size() <= whichMessage) {
            // return the last message in the thread
            return getMessageBody(t.getMessages().get(t.getMessages().size()-1));
        }
        // return the message queried for
        return getMessageBody(t.getMessages().get(whichMessage));
    }

    /**
     * @return the person who sent this message to the user
     */
    public String getFrom() {
        return getHeaderPart(FROM);
    }
    public String getDate()
    {
        return getHeaderPart(DATE);
    }
    public String getTo()
    {
        return getHeaderPart(TO);
    }
    public String getMailingList()
    {
        return getHeaderPart(MAILING_LIST);
    }

    /**
     * @return email address that can be used to reply to message
     */
    public String getReplyToAddress()
    {
        return getHeaderPart(REPLY_TO);
    }

    /**
     * @return Subject of the message
     */
    public String getSubject()
    {
        return getHeaderPart(SUBJECT);
    }

    /**
     * @return the person the message was sent to
     */
    public String getDeliveredTo()
    {
        return getHeaderPart(DELIVERED_TO);
    }

    /**
     * get the unsubscribe link from the mailing list
     * @return the link the user can go to to unsubscribe from this mailing list, if it exists
     */
    public String getUnsubscribeLink()
    {
        return getHeaderPart(LIST_UNSUBSCRIBE);
    }
    /**
     *
     * @param thread any thread
     * @return a thread with full payload
     * @throws IOException
     */
    private Thread getFullThreadInstance(Thread thread) throws IOException {
        return auth.service.users().threads().get(auth.userId, thread.getId()).execute();
    }

    public String getId() {
        return m.getId();
    }
}
