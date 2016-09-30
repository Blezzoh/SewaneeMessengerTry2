package de.email.core;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import de.email.aux.MessageParser;
import de.email.interfaces.Auth;
import de.email.interfaces.Mail;

import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 6/3/16.
 *
 * @author Daniel Evans
 */
public class FullMessage implements Mail {

    private static final String DATE = "Date";
    private static final String DELIVERED_TO = "Delivered-To";
    private static final String FROM = "From";
    private static final String TO = "To";
    private static final String REPLY_TO = "Reply-To";
    private static final String SUBJECT = "Subject";
    private static final String LIST_UNSUBSCRIBE = "List-Unsubscribe";
    private static final String MAILING_LIST = "Mailing-List";
    private static final String PAYLOAD_BODY_PARSER_STRING = "\"body\":{\"data\":\"";
    private static final String PROBLEM_TRASHING_MESSAGE = "There were problems trashing message with id ";


    private Message m;
    private Authenticator auth;

    public FullMessage(Authenticator auth, Message m) throws IOException {
        this.auth = auth;
        this.m = getFullMessagePayload(m);
    }
    public FullMessage(Auth auth, Message message) throws IOException {
        this(auth.getAuth(), message);
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
            System.out.println("DBMessage with id: " + msgId + " has been trashed.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(PROBLEM_TRASHING_MESSAGE + msgId);
        }
        return execute != null;
    }

    public Authenticator getAuth() {
        return auth;
    }

    public MessagePart getPayload() {
        return m.getPayload();
    }

    /**
     * @return returns true if the message was delete; false otherwise
     */
    public boolean trashMessage() {
        Message execute = null;
        try {
            execute = auth.service.users()
                    .messages().trash(auth.userId, m.getId()).execute();
            System.out.println("DBMessage with id: " + m.getId() + " has been trashed.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(PROBLEM_TRASHING_MESSAGE + m.getId());
        }
        return execute != null;
    }


    public String getBodyBase64String() {
        return getBodyBase64String(m, PAYLOAD_BODY_PARSER_STRING, m.getPayload().toString());
    }

    private String getBodyBase64String(Message m, String textToFind, String searchText) {
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
        return largest;
    }


    /**
     * gets the message with containing info specified by fields
     *
     * @return a full instance version of the message passed
     */
    public Message getFullMessageMetaData(Message message) {
        Preconditions.objectNotNull(message, Inbox.MESSAGE_NULL_ERROR);
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
     * @return the message with all body text, headers, links, images, etc
     */
    public Message getFullMessagePayload(Message message) {
        Preconditions.objectNotNull(message, Inbox.MESSAGE_NULL_ERROR);
        return getFullMessagePayload(message.getId());
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
     * @return the first couple words (about 20 words) in the email
     */
    public String getSnippet() {
        /*byte[] snip = Base64.decodeBase64(m.getSnippet());
        return m.getSnippet();*/
        try {
            if (getBody().length() < 20) return "";
            return MessageParser.getTextFromHTML(m.getSnippet());
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return "";
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
            if (header.getName().equals(part)) {
                return header.getValue();
            }
        }
        // no header found, return empty string
        return "";
    }

    public String getDate() {
        return getHeaderPart(DATE);
    }

    /**
     * @return the person who sent this message to the user
     */
    @Override
    public String getFromEmail() {
        return MessageParser.parseEmailAddress(getHeaderPart(FROM));
    }

    @Override
    public String getFromName() {
        return MessageParser.parseNameFromMessage(getHeaderPart(FROM));
    }

    @Override
    public String getBody() {
        return Inbox.decodeString(getBodyBase64String());
    }

    public String getTo() {
        return MessageParser.parseEmailAddress
                (getHeaderPart(TO));
    }

    public String getMailingList() {
        return getHeaderPart(MAILING_LIST);
    }

    /**
     * @return email address that can be used to reply to message
     */
    public String getReplyToAddress() {
        return MessageParser.parseEmailAddress(getHeaderPart(REPLY_TO));
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


}
