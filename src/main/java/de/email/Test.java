package de.email;

import SophiaMessenger.Models.DBMessage;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Daniel Evans on 7/13/16.
 * @author Daniel Evans
 */
public class Test {


    private static final String MESSAGE_ID = "1562ca4c4997819d";

    public static void main(String[] args) throws IOException, InterruptedException {

        Inbox inbox = new Inbox("iradub0@sewanee.edu");

        List<Message> msgs = inbox.getInbox();

        Message message = msgs.get(0);

        DBMessage m = null;
        try {
            m = new DBMessage(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
