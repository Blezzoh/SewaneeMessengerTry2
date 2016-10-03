package de.email.core;

import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;
import de.email.interfaces.Auth;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by daniel on 6/2/16.
 * @author Daniel Evans
 */
public class Inbox implements Auth {

    /**
     * the number of messages to retrieve on both a search and on startup of the application
     * BENCHMARK INFO: Connecting to server takes .5 seconds.
     * Each message takes 1/1000 of a second to list.
     */
    public static final int MESSAGE_SIZE = 50;
    static final String MESSAGE_NULL_ERROR = "param message is null";
    private static final String QUERY_NULL_ERROR = "param query is null";
    // Assuming you are sending email from localhost
    private static String host = "localhost";
    private Authenticator auth;
    private int retries = 0;

    /**
     * default constructor
     * sets up oAuth authentication and ensures access to all necessary
     * facilities of the user's account
     * @param emailAddress email address of the account to access
     */
    public Inbox(String emailAddress) {
        auth = new Authenticator(emailAddress);
    }

    /**
     * Helper method for decoding base64Encoded strings
     * @param base64String a base64 encoded string
     * @return decoded String in plain text
     */
    public static String decodeString(String base64String) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(base64String));
    }

    /**
     * Create a DBMessage from an email
     *
     * @param email DBMessage to be set to raw of message
     * @return DBMessage containing base64 encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     *
     * @return Session with the default system properties
     *
     */
    public static Session getSessionWithDefaultProps() {
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);
        return Session.getDefaultInstance(props);
    }

    private long expBackoff() {
        return Math.round(1000 * retries);
    }

    /**
     *
     * @return the authenticator for me's inbox
     */

    public Authenticator getAuth() {
        return auth;
    }

    /**
     * helper method that gets the message formatted with format=RAW
     * @param message doesn't assume this is a full instance method
     * @return returns the entire raw version of the message
     * @throws IOException
     */
    private Message getRawVersion(Message message) throws IOException {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        return auth.service.users().messages().get(auth.userId, message.getId()).set("format", "RAW").execute();
    }

    /**
     * gets the message with its full payload
     * @param message a list message
     * @return a full instance version of the message passed
     * @throws IOException
     */
    public Message getFullMessageInstance(Message message) throws IOException {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        return auth.service.users().messages().get(auth.userId, message.getId()).execute();
    }

    /**
     * search inbox for messages matching query
     * @param query searches user's inbox for messages using this query
     * @return returns a list of messages matching the query
     */
    public List<Message> listMessagesMatchingQuery(String query)
    {
        Preconditions.objectNotNull(query, QUERY_NULL_ERROR);
        ListMessagesResponse response = null;
        List<Message> messages = null;
        try {
            response = getMetadataResponse(query, null);

            messages = new ArrayList<>();
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    response = getMetadataResponse(query, response.getNextPageToken());
                    // make sure to modify the get inbox function
                    // if you remove the break or if statement below
                    if (messages.size() >= MESSAGE_SIZE) {
                        break;
                    }

                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * @return list of messages that contain their respective messageId and threadId or an empty list
     * @throws IOException
     */
    public List<Message> getInbox() {
        ListMessagesResponse response = null;
        try {
            response = getMetadataResponse(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Message> messages = new ArrayList<>(MESSAGE_SIZE);
        if (response != null) {
            System.out.println("Listing messages.... ");
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    try {
                        response = getMetadataResponse(null, response.getNextPageToken());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (messages.size() >= MESSAGE_SIZE)
                        break;

                } else
                    break;
            }
        }
        return messages;
    }

    private ListMessagesResponse getMetadataResponse(String query, String pageToken)
            throws IOException {
        // CALLS LIST METHOD, WHICH IS DIFFERENT FROM GET IN FULLMESSAGE
        Gmail.Users.Messages.List listMessages = auth.service.users().messages()
                .list(auth.userId)
                .set("format", "metadata")
                .set("metadataHeaders", "To")
                .set("metadataHeaders", "From")
                .set("metadataHeaders", "Date")
                .set("metadataHeaders", "Subject")
                .setQ("in:inbox"); // defaults to getting inbox info

        if (pageToken != null && query != null)
            return listMessages
                    .setQ(query)
                    .setPageToken(pageToken)
                    .execute();

        else if (query != null)
            return listMessages
                    .setQ(query)
                    .execute();

        else if (pageToken != null)
            return listMessages
                    .setPageToken(pageToken)
                    .execute();
        else
            return listMessages
                    .execute();
    }

    public Gmail getService() {
        return auth.service;
    }

    public String getUser() {
        return auth.userId;
    }

    public List<String> loadEmailAddresses(List<Message> inbox) {
        Preconditions.objectNotNull(inbox, "inbox is null");
        List<String> emailAddresses = null;
        try {
            emailAddresses = new ArrayList<>(25);
            for (int i = 0; i < inbox.size()/25; i++) {
                FullMessage fm = new FullMessage(this,inbox.get(i));
                if (!emailAddresses.contains(fm.getFromEmail()))
                    emailAddresses.add(fm.getFromEmail());
            }
            Collections.sort(emailAddresses);
        } catch (IOException e) {
        }
        return  emailAddresses;
    }

    /*public boolean isEmailAddress(String email) {
        String pattern = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*\n" +
                "@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$;";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(email);
        if(m.find()) {
            System.out.println("valid email address");
            return true;
        }
        System.out.println("not a valid email address");
        return false;
    }*/


    /**
     *
     * @param query searches user's inbox for threads using this query
     * @return list of threads matching query
     * @throws IOException
     */
    public List<Thread> listThreadsMatchingQuery(String query) throws IOException {
        Preconditions.objectNotNull(query, QUERY_NULL_ERROR);
        ListThreadsResponse response =
                auth.service.users().threads().list(auth.userId).setQ(query).execute();
        List<Thread> threads = new ArrayList<>();
        threads.addAll(response.getThreads());
        return threads;
    }

    /**
     *
     * @return list of threads matching query
     * @throws IOException
     */
    public List<Thread> listThreadsInInbox() throws IOException {
        ListThreadsResponse response =
                auth.service.users().threads().list(auth.userId).execute();
        List<Thread> threads = new ArrayList<>();
        threads.addAll(response.getThreads());
        return threads;
    }

    /**
     * List all Threads of the user's mailbox matching the query.
     * @throws IOException
     */
    public List<Thread> listThreads() throws IOException {
        ListThreadsResponse response =
                auth.service.users().threads().list(auth.userId).execute();
        List<Thread> threads = new ArrayList<>();
        while(response.getThreads() != null) {
            threads.addAll(response.getThreads());
            if(response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = auth.service.users().threads()
                        .list(auth.userId).setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        for(Thread thread : threads)
            System.out.println(thread.toPrettyString());

        return threads;
    }
    /**
     * List all Threads of the user's mailbox with labelIds applied.
     * @param labelIds String used to filter the Threads listed.
     * @throws IOException
     */
    public List<Thread> listThreadsWithLabels (List<String> labelIds) throws IOException {
        ListThreadsResponse response = auth.service.users()
                .threads().list(auth.userId).setLabelIds(labelIds).execute();
        List<Thread> threads = new ArrayList<>();
        while(response.getThreads() != null) {
            threads.addAll(response.getThreads());
            if(response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = auth.service.users()
                        .threads().list(auth.userId).setLabelIds(labelIds)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return threads;
    }
}