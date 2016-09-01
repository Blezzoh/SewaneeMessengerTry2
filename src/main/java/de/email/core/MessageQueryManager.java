package de.email.core;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;

import java.util.List;

/**
 * Created by daniel on 9/1/16.
 *
 * @author Daniel Evans
 */
public class MessageQueryManager {

    private Inbox inbox;
    private List<Message> queryMessages;

    public MessageQueryManager(Inbox inbox) {
        this.inbox = inbox;
    }

    public List<Message> retrieveMessages(String searchQuery) {
        Preconditions.objectNotNull(searchQuery, "searchQuery is null");
        queryMessages = inbox.listMessagesMatchingQuery(searchQuery);
        return queryMessages;
    }

    /**
     * @return false if no messages have been searched or if the search returned 0 messages, true otherwise
     */
    public boolean emptyMessages() {
        return queryMessages == null || queryMessages.size() == 0;
    }

    /**
     * @param label label that is associated with emails
     * @return list of messages associated with label
     */
    public List<Message> labelMessages(Label label) {
        Preconditions.objectNotNull(label, "label is null");
        return retrieveMessages(SearchQueries.LABEL + label.getName());
    }
}
