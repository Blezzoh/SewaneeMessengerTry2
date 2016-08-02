package com.danielevans.email;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import messenger_interface.Emailer;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.danielevans.email.Inbox.MESSAGE_NULL_ERROR;
import static com.danielevans.email.Inbox.decodeString;

/**
 * Created by daniel on 6/3/16.
 *
 * @author Daniel Evans
 */
public class FullMessage implements Auth, Emailer {

    private static final String DATE = "Date";
    private static final String DELIVERED_TO = "Delivered-To";
    private static final String FROM = "From";
    private static final String TO = "To";
    private static final String REPLY_TO = "Reply-To";
    private static final String SUBJECT = "Subject";
    private static final String LIST_UNSUBSCRIBE = "List-Unsubscribe";
    private static final String MAILING_LIST = "Mailing-List";
    private static final String TROUBLE_VIEWING_MESSAGE =
            "\n\nHaving trouble viewing this message? Sign in to your gmail account in a browser" +
                    " and go to this url: https://mail.google.com/mail/u/0/#inbox/";
    private static final String PAYLOAD_BODY_PARSER_STRING = "\"body\":{\"data\":\"";
    private static final String PROBLEM_TRASHING_MESSAGE = "There were problems trashing message with id ";
    private Message m;
    private Authenticator auth;
    public static final int REPLY = 0;
    public static final int FWD = 0;

    public FullMessage(Authenticator auth, Message message) throws IOException {
        this.auth = auth;
        this.m = getFullMessagePayload(message);
    }

    public FullMessage(Inbox inbox, Message message) throws IOException {
        this(inbox.getAuth(), message);
    }

    public FullMessage(Inbox inbox, String mId) throws IOException {
        this.auth = inbox.getAuth();
        this.m = getFullMessagePayload(mId);
    }

    public Authenticator getAuth() {
        return auth;
    }

    /**
     * @return the message body in HTML
     */
    public String getMessageHTML() {
        Preconditions.objectNotNull(m, MESSAGE_NULL_ERROR);
        // try to get the html from a the message parts in m's payload
        try {
            String html = Inbox.decodeString(m.getPayload().getParts().get(1)
                    .getBody().getData());
            if (html != null) {
                System.out.println(1);
                return html;
            }
        } catch (Exception ignored) {
        }
        try {
            String html = Inbox.decodeString(m.getPayload().getParts().get(0)
                    .getParts().get(0).getParts().get(0).getBody().getData());
            if (html != null) {
                System.out.println(2);
                return html;
            }
        } catch (Exception ignored) {
        }
        try {
            String html = m.getPayload().getParts().get(0)
                    .getParts().get(0).getBody().getData();
            if (html != null) {
                System.out.println(3);
                return Inbox.decodeString(html);
            }
        } catch (Exception ignored) {
        }
        try {
            String text = m.getPayload().getBody().getData();
            if (text != null) {
                System.out.println(4);
                return Inbox.decodeString(text + "\n");
            }
        } catch (Exception ignored) {
        }
        try {
            String text = m.getPayload().getParts().get(0).getBody().getData();
            if (text != null) {
                System.out.println(5);
                return Inbox.decodeString(text + "\n");
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * @param msgId id of message to delete
     * @return returns true if the message was delete; false otherwise
     * @throws IOException
     */
    public static boolean trashMessage(Inbox inbox, String msgId) {
        Message execute = null;
        try {
            execute = inbox.getAuth().service.users()
                    .messages().trash(inbox.getAuth().userId, msgId).execute();
            System.out.println("Message with id: " + msgId + " has been trashed.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(PROBLEM_TRASHING_MESSAGE + msgId);
        }
        return execute != null;
    }

    /**
     * @return returns true if the message was delete; false otherwise
     */
    public boolean trashMessage() {
        Message execute = null;
        try {
            execute = auth.service.users()
                    .messages().trash(auth.userId, m.getId()).execute();
            System.out.println("Message with id: " + m.getId() + " has been trashed.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(PROBLEM_TRASHING_MESSAGE + m.getId());
        }
        return execute != null;
    }


    public String getBestMessageBody() {
        return getBestMessageBody(m, PAYLOAD_BODY_PARSER_STRING, m.getPayload().toString());
    }

    private String getBestMessageBody(Message m, String textToFind, String searchText) {
        int len = textToFind.length();
        int k = 0;
        int[] indexes = new int[10];
        for (int i = 0; i < searchText.length(); i++) {
            int j = searchText.indexOf(textToFind, i);
            if (j == -1)
                break;
            else {
                indexes[k++] = j;
                i = j + len;
            }
        }
        int[] quoteIndices = new int[k];
        for (int i = 0; i < indexes.length && indexes[i] != 0; i++) {
            quoteIndices[i] = searchText.indexOf("\"", indexes[i] + len);
        }
        // the largest string is the most likely string to contain HTML when decoded
        String largest = "";
        for (int i = 0; i < quoteIndices.length && quoteIndices[i] != 0; i++) {
            String curr = m.getPayload().toString().substring(indexes[i] + len, quoteIndices[i]);
            if (curr.length() > largest.length())
                largest = curr;
        }
        // decode the largest string
        return Inbox.decodeString(largest);
    }

    // </?\w+((\s+\w+(\s*=\s*(?:".*?"|'.*?'|[\^'">\s]+))?)+\s*|\s*)/?>
    public static boolean testForHTML(String largest) {
        String r1 = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[\\^'\">\\s]+))?)+\\s*|\\s*)/?>";
        Pattern p1 = Pattern.compile(r1);
        for (int i = 0; i + 1 < largest.length(); ) {
            int i1 = largest.indexOf("<", i);
            int i2 = largest.indexOf(">", i1);
            if (i1 == -1 || i2 == -1 || i2 == largest.length() - 1)
                return false;

            if (p1.matcher(largest.substring(i1, i2 + 1)).matches()) {
                return true;
            }
            i = i2 + 1;
        }
        return false;
    }

    private String getMessageBodyAsHTML(Message message) throws IOException {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        // try to get the html from a the message parts in m's payload
        Message m = getFullMessagePayload(message);
        try {
            String html = Inbox.decodeString(m.getPayload().getParts().get(1)
                    .getBody().getData());
            if (html != null) {
                return html;
            }
        } catch (Exception ignored) {
        }
        try {
            String html = m.getPayload().getParts().get(0)
                    .getParts().get(0).getBody().getData();
            if (html != null) {
                return Inbox.decodeString(html);
            }
        } catch (Exception ignored) {
        }
        try {
            String text = m.getPayload().getBody().getData();
            if (text != null) {
                return Inbox.decodeString(text + "\n");
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String getMessageBodyAsHTML() throws IOException {
        return getMessageBodyAsHTML(m);
    }


    /**
     * gets the message with containing info specified by fields
     *
     * @return a full instance version of the message passed
     */
    public Message getFullMessageMetaData(Message message) {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        try {
            // set fields
            return auth.service.users().messages()
                    // NOTE THE GET
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

    /**
     * if you need the full message content from some email message,
     * this is the method to use
     *
     * @return the message with all body text, headers, links, images, etc
     */
    public Message getFullMessagePayload(Message message) {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        try {
            return auth.service.users().messages()
                    .get(auth.userId, message.getId())
                    .execute();
        } catch (IOException e) {
            System.out.println("CANNOT RETRIEVE THE MESSAGE");
            e.printStackTrace();
            return null;
        }
    }

    private Message getFullMessagePayload(String mId) {
        try {
            return auth.service.users().messages()
                    .get(auth.userId, mId)
                    .execute();
        } catch (IOException e) {
            System.out.println("CANNOT RETRIEVE THE MESSAGE");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * if you have a bunch of full messages only containing the metadata,
     * but you need the full content, use this method
     *
     * @return the message with all body text, headers, links, images, etc
     */
    private Message getFullMessagePayload(FullMessage message) {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        return getFullMessagePayload(message.getId());
    }

    /**
     * @return the first couple words (about 20 words) in the email
     */

    public String getSnippet() {
        return m.getSnippet();
    }

    /**
     * gets the decoded raw version of the email, formatted with format=RAW
     *
     * @return returns the entire raw version (contains HTML) of the message
     */
    public Message getRawVersion() {
        try {
            return auth.service.users().messages().get(auth.userId, m.getId()).set("format", "RAW").execute();
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
        /*
         * we have to loop through the headers because they are
         * different on every message, so linear search for the
         * messge is the only option. Tried to use the json object
         * as a hashmap (more or less) but it didn't work
         * (returned null each time)
         */
        List<MessagePartHeader> headers = m.getPayload().getHeaders();
        for (MessagePartHeader header : headers) {
            // TESTING
//            System.out.println(header);
            if (header.getName().equals(part)) {
                // when we find the correct header (key),
                // return its value
                return header.getValue();
            }
        }
        // no header found, return empty string
        return "";
    }

    /**
     * @return returns the text of the email
     */
    public String getMessageBody() {
        // note that this will throw a null pointer exception
        // if the message only contains html
        // that is m.getPayload.getParts will be null
        // TODO: Fix the above comments
        return getMessageBody(m);
    }

    /**
     * @param message message to retrieve body text from
     * @return returns the text of the email
     */
    public String getMessageBody(Message message) {
        String mId = message.getId();
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        // note that this will throw a null pointer exception
        // if the message only contains html
        // that is m.getPayload.getParts will be null
        // TODO: Fix the above comments
        try {
            String text = decodeString(message.getPayload().getParts()
                    .get(0).getBody().getData());
            if (text != null) {
                return text + TROUBLE_VIEWING_MESSAGE;
            }
        } catch (NullPointerException e) {
        }
        String emailBody = null;
        Message z = null;
        try {
            z = getFullMessagePayload(message);
            emailBody = z.getPayload().getParts()
                    .get(0).getBody().getData();
            if (emailBody != null)
                return Inbox.decodeString(emailBody) + TROUBLE_VIEWING_MESSAGE + mId;
        } catch (NullPointerException ignored) {
        }
        try {
            String text = getMessageBodyAsHTML(z);
            if (text != null) {
                return text + TROUBLE_VIEWING_MESSAGE + mId;
            }
        } catch (IOException ignored) {
        }
        try {
            emailBody = Inbox.decodeString(z.getPayload()
                    .getParts().get(0).getParts().get(0).getBody().getData());
        } catch (NullPointerException ignored) {
        }
        if (emailBody != null)
            return emailBody + TROUBLE_VIEWING_MESSAGE + mId;

        return "";
    }


    /**
     * @return the person who sent this message to the user
     */
    public String getFrom() {
        return getHeaderPart(FROM);
    }

    public String getDate() {
        return getHeaderPart(DATE);
    }

    public String getTo() {
        return getHeaderPart(TO);
    }

    public String getMailingList() {
        return getHeaderPart(MAILING_LIST);
    }

    /**
     * @return email address that can be used to reply to message
     */
    public String getReplyToAddress() {
        return getHeaderPart(REPLY_TO);
    }

    /**
     * @return Subject of the message
     */
    public String getSubject() {
        return getHeaderPart(SUBJECT);
    }

    /**
     * @return the person the message was sent to
     */
    public String getDeliveredTo() {
        return getHeaderPart(DELIVERED_TO);
    }

    /**
     * get the unsubscribe link from the mailing list
     *
     * @return the link the user can go to to unsubscribe from this mailing list, if it exists
     */
    public String getUnsubscribeLink() {
        return getHeaderPart(LIST_UNSUBSCRIBE);
    }

    public String getId() {
        return m.getId();
    }

    public Message getM() {
        return m;
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

    @Override
    public MimeMessage composeMessage(Session session,
                                      String to,
                                      String subject,
                                      String message) throws MessagingException {
        Preconditions.objectNotNull(session, "session is null");
        Preconditions.objectNotNull(to, "to is null");
        Preconditions.objectNotNull(subject, "subject is null");
        Preconditions.objectNotNull(message, "message is null");
        MimeMessage m = new MimeMessage(session);
        m.setFrom(new InternetAddress(this.auth.userId));

        // TODO: if CC/BCC contains an email address, iterate through the
        // TODO: email addresses using this method
        m.addRecipients(javax.mail.Message.RecipientType.CC, to);
        m.setSubject(subject);
        m.setText(message);
        return m;

    }

    @Override
    public void sendMessage(MimeMessage email) throws MessagingException, IOException {

    }
}
