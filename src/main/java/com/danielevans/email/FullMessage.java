package com.danielevans.email;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.api.services.gmail.model.Thread;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.danielevans.email.Inbox.MESSAGE_NULL_ERROR;
import static com.danielevans.email.Inbox.decodeString;

/**
 * Created by daniel on 6/3/16.
 * @author Daniel Evans
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
    // provides a message to the user if the email whose body they are trying to read is
    // not available (presumably for security reasons), gives a link to the email on Gmail site
    private static final String SECURITY_EMAIL_MESSAGE =
            "This message was marked as secure. To read it, sign into your Gmail account " +
                    "and go to this url: https://mail.google.com/mail/u/0/#inbox/";
    private static final String TROUBLE_VIEWING_MESSAGE =
            "\n\nHaving trouble viewing this message? Sign in to your gmail account in a browser" +
                    " and go to this url: https://mail.google.com/mail/u/0/#inbox/";
    private static final String PAYLOAD_BODY_PARSER_STRING = "\"body\":{\"data\":\"";
    private Message m;
    private Authenticator auth;

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
        } catch (Exception ignored) { }
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
        } catch (Exception ignored) { }
        try {
            String text = m.getPayload().getBody().getData();
            if (text != null) {
                System.out.println(4);
                return Inbox.decodeString(text + "\n");
            }
        } catch (Exception ignored) { }
        try {
            String text = m.getPayload().getParts().get(0).getBody().getData();
            if (text != null) {
                System.out.println(5);
                return Inbox.decodeString(text + "\n");
            }
        } catch (Exception ignored) { }
        return null;
    }

    public String getBestMessageBody() {
        return getBestMessageBody(m, PAYLOAD_BODY_PARSER_STRING, m.getPayload().toString());
    }

    public String getBestMessageBody(Message m) {
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
     *
     * @return the first couple words (about 20 words) in the email
     */

    public String getSnippet() {
        return m.getSnippet();
    }

    /**
     * gets the decoded raw version of the email, formatted with format=RAW
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
     *
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
        // message does not have a body, so provide the link to the email on gmail site
        return SECURITY_EMAIL_MESSAGE + mId;
    }
    /**
     *
     * @param thread thread to take the message from
     * @param whichMessage number of the message in the thread, 0 = first message
     *                     1 = second message, etc
     * @return the body of the specified message in the thread, or the body
     * of the last message if whichMessage greater than thread.getMessages().size()
     * @throws IOException
     */
    public String getBodyOfMessageInThread(Inbox inbox, Thread thread, int whichMessage)
            throws IOException {

        if(thread == null) throw new NullPointerException("message is null");

        FullThread t = new FullThread(inbox, thread);
        // if user requests a message that doesn't exist in thread (ex: only 2 messages
        // in the thread, user requests the third), then return the last message
        if(t.getMessages().size() <= whichMessage) {
            // return the last message in the thread
            return getBestMessageBody(t.getMessages().get(t.getMessages().size()-1));
        }
        // return the message queried for
        return getBestMessageBody(t.getMessages().get(whichMessage));
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

    public String getId() {
        return m.getId();
    }

    public Message getM() {
        return m;
    }

    /**
     * Modify the labels a message is associated with.
     *
     * @param service        Authorized Gmail API instance.
     * @param userId         User's email address. The special value "me"
     *                       can be used to indicate the authenticated user.
     * @param messageId      ID of Message to Modify.
     * @param labelsToAdd    List of label ids to add.
     * @param labelsToRemove List of label ids to remove.
     * @throws IOException
     */
    public static void modifyMessage(Gmail service, String userId, String messageId,
                                     List<String> labelsToAdd, List<String> labelsToRemove) throws IOException {
        ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(labelsToAdd)
                .setRemoveLabelIds(labelsToRemove);
        Message message = service.users().messages().modify(userId, messageId, mods).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
    }
}
