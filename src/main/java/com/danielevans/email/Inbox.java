package com.danielevans.email;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by daniel on 6/2/16.
 * @author Daniel Evans
 */
public class Inbox {

    /**
     * the number of messages to retrieve on both a search and on startup of the application
     */
    public static final int MESSAGE_SIZE = 100;
    static final String MESSAGE_NULL_ERROR = "message is null";
    static final String QUERY_NULL_ERROR = "query is null";
    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart.json
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS
                    , GmailScopes.GMAIL_COMPOSE
                    ,GmailScopes.GMAIL_MODIFY);
    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;
    // Recipient's email ID needs to be mentioned.
    private String me = "evansdb0@sewanee.edu";
    // Assuming you are sending email from localhost
    private String host = "localhost";

    private List<Message> inbox;
    private Authenticator auth;

    /**
     * default constructor
     * sets up oAuth authentication and ensures access to all neccessary
     * facilities of the user's account
     * @param auth Authenticator that performs the oauth authentication
     */
    public Inbox(Authenticator auth) {
        auth.userId = "me";
        this.auth = auth;
        try {
            inbox = getInbox();
        } catch (IOException e) {
            System.out.println("Problem getting email messages... Internet connection?");
            e.printStackTrace();
        }
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
                    if (messages.size() > MESSAGE_SIZE) {
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
    public List<Message> getInbox() throws IOException {
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
    public Session getSessionWithDefaultProps() {
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host",host);
        return Session.getDefaultInstance(props);
    }

    /**
     *
     * @param session most cases use getSessionWithDefaultProps
     * @param to email address of receiver
     * @param subject subject of message
     * @param message body of the message
     * @return composed MimeMessage
     * @throws MessagingException
     */
    public MimeMessage composeMessage(Session session,
                                      String to,
                                      String subject,
                                      String message) throws MessagingException {

        MimeMessage m = new MimeMessage(session);
        m.setFrom(new InternetAddress(me));

        // TODO: if CC/BCC contains an email address, iterate through the
        // TODO: email addresses using this method
        m.addRecipients(javax.mail.Message.RecipientType.CC,to);
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
                if(!emailAddresses.contains(fm.getFrom()))
                    emailAddresses.add(MessageParser.parseEmailAddress(fm.getFrom()));
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

    /*/**
     *
     * @param thread any thread
     * @return a thread with full payload
     * @throws IOException
     *
    public Thread getFullThreadInstance(Thread thread) throws IOException {
        return auth.service.users().threads().get(auth.userId, thread.getId()).execute();
    }
    */
}