package com.danielevans.email;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;
import messenger_interface.Emailer;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
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
public class Inbox implements Auth, Emailer {

    /**
     * the number of messages to retrieve on both a search and on startup of the application
     */
    public static final int MESSAGE_SIZE = 100;
    static final String MESSAGE_NULL_ERROR = "param message is null";
    private static final String QUERY_NULL_ERROR = "param query is null";
    // Assuming you are sending email from localhost
    private static String host = "localhost";
    private List<Message> inbox;
    private Authenticator auth;
    private int retries = 0;
    private static int NO_INTERNET_CONNECTION = 137;

    /**
     * default constructor
     * sets up oAuth authentication and ensures access to all necessary
     * facilities of the user's account
     * @param emailAddress email address of the account to access
     */
    public Inbox(String emailAddress) {
        auth = new Authenticator(emailAddress);
//        auth.userId = "me";
        boolean retry = true;
        // if connection fails during listMessages() call, retry the connection
        // up to 5 times with exponential backoff
        while (retry) {
            try {
                inbox = listMessages();
                retry = false; // kill the while loop because getInbox() never threw
            } catch (IOException e) {
                ++retries;
                System.out.println("Failed to connect on try " + retries +
                        ". Retrying now...");
                try {
                    // server is possibly receiving a lot of traffic
                    // so wait a little while before trying again
                    // via exponential backoff
                    java.lang.Thread.sleep(expBackoff());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (retries == 5) {
                    System.out.println("Problem getting email messages. " +
                            "Aborting connection. Are you connected to the internet?");
                    System.exit(NO_INTERNET_CONNECTION);
                }
                e.printStackTrace();
            }
        }
        System.out.println("Connection established");
    }

    private long expBackoff() {
        return Math.round(1000 * retries);
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
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64 encoded email.
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
     * @return the list of messages in the user's inbox. Note that these are not
     * full instance messages
     */
    public List<Message> getDefaultInbox() {
        return inbox;
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
        System.out.println("Inbox: message search size = " + messages.size());
        return messages;
    }


    /**
     * @return list of messages that contain their respective messageId and threadId
     * @throws IOException
     */
    public List<Message> listMessages() throws IOException {
        ListMessagesResponse response = getMetadataResponse(null, null);

        List<Message> messages = new ArrayList<>(MESSAGE_SIZE);
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                response = getMetadataResponse(null, response.getNextPageToken());
                // make sure to modify the get inbox function
                // if you remove the break or if statement below
                if (messages.size() >= MESSAGE_SIZE) {
                    break;
                }
            } else {
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
                .set("metadataHeaders", "Subject");

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

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * can be used to indicate the authenticated user.
     * @param email Email to be sent.
     * @throws MessagingException
     * @throws IOException
     */
    public void sendMessage(MimeMessage email)
            throws MessagingException, IOException {
        Preconditions.objectNotNull(email, "email is null");
        Message message = createMessageWithEmail(email);
        message = auth.service.users().messages().send(auth.userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
    }

    /**
     *
     * @return Session with the default system properties
     *
     */
    public static Session getSessionWithDefaultProps() {
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host",host);
        return Session.getDefaultInstance(props);
    }

    /**
     *
     * @param to email address of receiver
     * @param subject subject of message
     * @param message body of the message
     * @return composed MimeMessage
     * @throws MessagingException
     */
    public MimeMessage composeMessage(String to,
                                      String subject,
                                      String message) throws MessagingException {

        MimeMessage m = new MimeMessage(getSessionWithDefaultProps());
        m.setFrom(new InternetAddress(this.auth.userId));

        // TODO: if CC/BCC contains an email address, iterate through the
        // TODO: email addresses using this method
        m.addRecipients(MimeMessage.RecipientType.CC, to);
        m.setSubject(subject);
        m.setText(message);
        return m;
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