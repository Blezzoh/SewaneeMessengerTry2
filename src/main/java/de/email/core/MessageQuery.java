package de.email.core;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;

import java.util.List;

/**
 * Created by daniel on 9/1/16.
 * @author Daniel Evans
 */
public class MessageQuery {

    private Inbox inbox;
    private List<Message> queryMessages;

    /**
     * @param inbox the user's inbox
     */
    public MessageQuery(Inbox inbox, String searchQuery) {
        Preconditions.objectNotNull(inbox, "inbox cannot be null");
        Preconditions.objectNotNull(searchQuery, "searchQuery is null");
        this.inbox = inbox;
        queryMessages = inbox.listMessagesMatchingQuery(searchQuery);
    }

    /**
     * @param label label that is associated with emails
     * @return list of messages associated with label
     */
    public MessageQuery(Inbox inbox, Label label) {
        this(inbox, SearchQueries.LABEL + label.getName());
    }

    public List<Message> retrieveMessages() {
        return queryMessages;
    }

    /**
     * @return false if no messages have been searched or if the search returned 0 messages, true otherwise
     */
    public boolean emptyMessages() {
        return queryMessages.size() == 0;
    }

}
